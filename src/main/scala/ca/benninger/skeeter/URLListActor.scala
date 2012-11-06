
package ca.benninger.skeeter

import akka.actor.Actor
import akka.actor.Props
import akka.event.Logging

class URLListActor extends Actor{

	var unvisited:Map[String,Boolean] = null
	var visited:Map[String,Boolean] = null

    override def preStart = {
        println("Starting URLListActor...")
        unvisited = Map[String,Boolean]()
        visited = Map[String,Boolean]()
    }
    
    def receive = {

    	case NewURLList(urls) =>
    		for( ( k,v ) <- urls ){
    			if( !unvisited.contains(k) && !visited.contains(k) ) {
    				unvisited += k -> v
    			}
    		}

		case e:URLGetRequest =>
			val (u,v) = unvisited.head
			unvisited = unvisited - u
			sender ! new URLGetResponse(u)

        case e => println("Unexpected Message: "+e)
    }


}