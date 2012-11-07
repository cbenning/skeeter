
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
        unvisited += "http://www.mec.ca/AST/ShopMEC/Travel/HostellingGames/PRD~5010-891/hostelling-international-canada-membership-package.jsp" -> false
        unvisited += "http://www.mec.ca/AST/ShopMEC/Cycling/Bikes/Urban/PRD~5020-468/mec-hold-steady-bicycle-unisex.jsp" -> false
    }
    
    def receive = {

    	case NewURLList(urls) =>
    		for( ( k,v ) <- urls ){
    			if( !unvisited.contains(k) && !visited.contains(k) ) {
    				unvisited += k -> v
    			}
    		}
            println("Processed URLs: "+visited.size+" , Unvisited URLs: "+unvisited.size)

		case e:URLGetRequest =>
            if(unvisited.size > 0){
    			val (u,v) = unvisited.head
    			unvisited = unvisited - u
                visited += u -> false
    			sender ! new URLGetResponse(u)
            }

        case e => println("Unexpected Message: "+e)
    }


}