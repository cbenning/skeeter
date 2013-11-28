
package ca.benninger.skeeter

import akka.actor._
import akka.actor.Props
import akka.event.Logging

object Scraper extends App {

    var scrapers = List[ActorRef]()

    val system = ActorSystem("ScraperSystem")
    val urlListActor = system.actorOf(Props[URLListActor], "URLList")
    val productListActor = system.actorOf(Props[ProductListActor], "ProductList")
    

    val max = 100
    var i = 0

   	while( i < max){
   		//scrapers = scrapers + 
	    system.actorOf(Props(new ScraperActor(urlListActor,productListActor)), "Scraper-"+i.toString)
	    i += 1
	}

}
