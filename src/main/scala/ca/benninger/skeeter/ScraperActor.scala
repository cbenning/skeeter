
package ca.benninger.skeeter


import org.ccil.cowan.tagsoup.jaxp.SAXFactoryImpl
import scala.xml.{Elem, XML}
import scala.xml.factory.XMLLoader
import akka.actor.Actor
import akka.actor.Props
import akka.event.Logging

class ScraperActor extends Actor{

	val parserFactory = new SAXFactoryImpl

 


    override def preStart = {
        println("Starting ScraperActor..")
    }
    
    def receive = {
    	
        case URLGetResponse(url) => 

        	try{
				val parser = parserFactory.newSAXParser()
				val source = new org.xml.sax.InputSource(url)
				val adapter = new scala.xml.parsing.NoBindingFactoryAdapter
				adapter.loadXML(source, parser)


			}

    }

}
