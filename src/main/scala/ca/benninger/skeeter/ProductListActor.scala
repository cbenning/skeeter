
package ca.benninger.skeeter

import akka.actor.Actor
import akka.actor.Props
import akka.event.Logging
import scala.io._
import com.mongodb.casbah.Imports._

class ProductListActor extends Actor{

    var products:Map[String,NewProduct] = null
    var out_file:java.io.FileOutputStream = null
    var out_stream:java.io.PrintStream = null
    val server = new ServerAddress("boerealis.com",27017)
    val credentials = MongoCredential("externalproductsfrom","productsfrom","nopenopenope".toArray)
    val mongoClient = MongoClient(server, List(credentials))
    val db = mongoClient("productsfrom")
    val col = db("productsfrom")

    override def preStart = {
        println("Starting ProductListActor...")
        products = Map[String,NewProduct]()
        out_file = new java.io.FileOutputStream("products.csv")
        out_stream = new java.io.PrintStream(out_file)
    }
    
    def receive = {

    	case p:NewProduct =>
         //products += p.name -> p
        val newObj = MongoDBObject.newBuilder
                      
        newObj += "name" -> p.name
        newObj += "origin" -> p.madein
        newObj += "price" -> p.price
        newObj += "remoteurl" -> p.url
        newObj += "remoteimg" -> p.imgURL
        newObj += "reviewurl" -> p.reviewURL
        newObj += "desc" -> p.desc
        if(col.find(newObj.result).count < 1){
          col.insert(newObj.result)
        }
        // println("Products: "+products.size)
        // out_stream.print(p.name+","+p.madein+","+p.price+","+p.reviewURL+","+p.imgURL+","+p.url+"\n")

        case e => println("Unexpected Message: "+e)
    }

}
