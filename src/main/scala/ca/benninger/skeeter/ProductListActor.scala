
package ca.benninger.skeeter

import akka.actor.Actor
import akka.actor.Props
import akka.event.Logging
import scala.io._

class ProductListActor extends Actor{

	var products:Map[String,NewProduct] = null
    var out_file:java.io.FileOutputStream = null
    var out_stream:java.io.PrintStream = null

    override def preStart = {
        println("Starting ProductListActor...")
        products = Map[String,NewProduct]()
        out_file = new java.io.FileOutputStream("products.csv")
        out_stream = new java.io.PrintStream(out_file)
    }
    
    def receive = {

    	case p:NewProduct =>
			//products += p.name -> p
            println("Products: "+products.size)
            out_stream.print(p.name+","+p.madein+","+p.price+","+p.reviewURL+","+p.imgURL+","+p.url+"\n")

        case e => println("Unexpected Message: "+e)
    }

}