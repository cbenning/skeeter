
package ca.benninger.skeeter

import akka.actor._
import akka.actor.Props
import akka.event.Logging

object Scraper extends App {

	var scrapers = List[ActorRef]()

    val system = ActorSystem("ScraperSystem")
    val urlListActor = system.actorOf(Props[URLListActor], name = "URLList")

    val max = 10
    var i = 0

   	while( i < max){
   		//scrapers = scrapers + 
	    system.actorOf(Props[ScraperActor], name = "Scraper-"+i.toString)
	    i += 1
	}

}