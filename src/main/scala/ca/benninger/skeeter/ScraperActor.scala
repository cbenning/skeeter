
package ca.benninger.skeeter

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import scala.collection.JavaConversions._
import akka.actor._
import akka.event.Logging
import scala.util.matching.Regex
import scala.util.matching.Regex._
import akka.actor.ReceiveTimeout
import akka.util.duration._

class ScraperActor(urllist:ActorRef,productlist:ActorRef) extends Actor{

    override def preStart = {
        println("Starting ScraperActor..")
        this.urllist ! new URLGetRequest
        context.setReceiveTimeout(3 seconds)
    }
    
    def receive = {
        case URLGetResponse(url) =>
            val regex = new Regex("(http://www.mec.ca|/AST)")
            regex findFirstIn url match{
                case Some(regex) =>
                    processURL(url) 
                case None => 0
            }
            this.urllist ! new URLGetRequest
            context.setReceiveTimeout(3 seconds)

        case ReceiveTimeout =>
            this.urllist ! new URLGetRequest
            context.setReceiveTimeout(3 seconds)
    }

    def processURL(url:String) = {
        try{
            val doc = Jsoup.connect(url).timeout(0).get

            //println("Processing: "+url)

            //Scrape links
            val links = doc.select("a")
            var tolook = Map[String,Boolean]()

            //
            // regex findFirstIn url match{
            //     case Some(regex) =>  
            //         tolook += new_url -> false
            //     case None => 0
            // }

            //Search for valid urls
            for ( link <- links) {
                val _url = link.attr("href")
                val regex_http_mec = new Regex("http://www.mec.ca/")
                regex_http_mec findFirstIn _url match{

                    case Some(regex_http_mec) =>  
                        tolook += _url -> false

                    case None =>
                        val regex_mec = new Regex("/product/")
                        regex_mec findFirstIn _url match{

                            case Some(regex_mec) =>  
                                val new_url = "http://www.mec.ca"+_url
                                tolook += new_url -> false

                            case None => 0
                        }


                }
            }

            //If we found urls, submit them
            if(tolook.size>0){
                this.urllist ! new NewURLList(tolook)
            }

            //Scrape data
            val madeIn = doc.select("span.prDs.CAN").text
            //val madeIn = doc.select("span.prDs").text
            if(madeIn.contains("Made in Canada")) {
                val root = doc.select("div#shopbox")
                val name = root.select("h1").text
                val price = root.select("div#idPrdPrice span.prPr").text
                val reviewLink = root.select("div.merch-rating a:eq(1)").attr("href")
                val image = root.select("div#skuColours a.cloud-zoom-gallery").attr("href")
                val desc = root.select("div.longdesc").text
                println(" ---> Found one!: "+name)
                this.productlist ! new NewProduct(madeIn,name,price,reviewLink,image,url,desc)
            }
            // else {
            //     if(madeIn != ""){
            //         println("Product was : "+madeIn)
            //     }
            //     else{
            //         println("Not a product page")   
            //     }
            // }
        }
        catch{
            case e:Throwable => 0
        }
    }

}
