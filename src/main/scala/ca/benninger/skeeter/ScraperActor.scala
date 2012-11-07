
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

// import org.ccil.cowan.tagsoup.jaxp.SAXFactoryImpl
// import scala.xml.{Elem, XML}
// import scala.xml.factory.XMLLoader

class ScraperActor extends Actor{

	//val parserFactory = new SAXFactoryImpl
    var urllist:ActorRef = null

    def this(urllist:ActorRef) = {
        this()
        this.urllist = urllist
    }

    override def preStart = {
        println("Starting ScraperActor..")
        this.urllist ! new URLGetRequest
        context.setReceiveTimeout(3 seconds)
    }
    
    def receive = {
    	
        case URLGetResponse(url) => 

            try{
                val doc = Jsoup.connect(url).timeout(0).get

                //Scrape links
                val links = doc.select("a")
                var tolook = Map[String,Boolean]()
                val domain = "http://www.mec.ca"
                val regex = new Regex(domain+"/AST/")

                regex findFirstIn url match{
                    case Some(regex) =>  
                        tolook += new_url -> false
                    case None => 0
                }

                for ( link <- links) {
                    val new_url = domain+link.attr("href")
                    regex findFirstIn new_url match{
                        case Some(regex) =>  
                            tolook += new_url -> false
                        case None => 0
                    }
                }


                if(tolook.size>0){
                    this.urllist ! new NewURLList(tolook)
                }


                //Scrape data
                val madeIn = doc.select("span.prDs").text
                if(madeIn.contains("Made in Canada")) {
                    val root = doc.select("div#shopbox")
                    val name = root.select("h1").text
                    val price = root.select("div#idPrdPrice span.prPr").text
                    val reviewLink = root.select("div.merch-rating a:eq(1)").attr("href")
                    val image = root.select("div#skuColours a.cloud-zoom-gallery").attr("href")
                    println("Found one!: "+name)
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
                case e => println(e)
            }

            this.urllist ! new URLGetRequest
            context.setReceiveTimeout(3 seconds)

        case ReceiveTimeout =>
            this.urllist ! new URLGetRequest
            context.setReceiveTimeout(3 seconds)
    }

}
