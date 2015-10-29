/*
Project #5

Due Dates:  Wednesday, April 29 at 11:59pm 

Objective:
    Work with Dijkstra's single-source shortest path algorithm.


Overview:
    In this project you will use Dijkstra's algorithm to find route information between two airports
    chosen by the user.  The user will be shown a route that results in the lowest price.


Details:
    Create a class called Dijkstra.

    The structure of this class in terms of inner classes and methods will be up to you.
    Points will be reduced if your program is not well organized and commented.

    Basic requirements are:
      4) Repeats until the user chooses to exit.  

    Sample output:

    Enter departure airport:  DFW
    Enter arrival airport:    SFO 

    Price:       1100
    Connections: 1
    Route:       DFW -> LAX -> SFO

    Check another route (Y/N)? 


Submit to eLearning:
     Dijkstra.java
*/

import java.util.*;
import java.io.*;

class Vertex implements Comparable<Vertex>{
    public final String name;
    public ArrayList<Edge> adjacencies = new ArrayList<Edge>();	//ArrayList of the Edges that this vertex has
    public int minDistance = Integer.MAX_VALUE;
    public Vertex previous;
    public Vertex(String argName) { name = argName; }
    public String toString() { return name; }

    //Recommended by research
    public int compareTo(Vertex other){
        return Double.compare(minDistance, other.minDistance);
    }
}

class Edge{
    public final Vertex target;
    public final int weight;
    public Edge(Vertex argTarget, int argWeight)
    { target = argTarget; weight = argWeight; }
}

public class Dijkstra
{
	static ArrayList<Vertex> vertices = new ArrayList<Vertex>();	// ArrayList of all the vertices in the file
	
	/** Calculates the paths in a graph from the passed Vertex source */
    public static void computePaths(Vertex source)
    {
    	//Clear all previous information
    	for(Vertex asdf : vertices){
    		asdf.minDistance = Integer.MAX_VALUE;
    		asdf.previous = null;
    	}
    	
        source.minDistance = 0;
        PriorityQueue<Vertex> vertexQueue = new PriorityQueue<Vertex>();
      	vertexQueue.add(source);
      	
		while (!vertexQueue.isEmpty()) {
		    Vertex u = vertexQueue.poll();
		    
	        // Visit each edge exiting u
	        for (Edge e : u.adjacencies)
	        {
	            Vertex v = e.target;
	            int weight = e.weight;
	            int distanceThroughU = u.minDistance + weight;
	            if (distanceThroughU < v.minDistance) {
				    vertexQueue.remove(v);
				    v.minDistance = distanceThroughU ;
				    v.previous = u;
				    vertexQueue.add(v);
				}
	        }
		}
    }
    
    /** Gets the shortest path from the previously calculated source to the target*/
    public static List<Vertex> getShortestPathTo(Vertex target){
        List<Vertex> path = new ArrayList<Vertex>();
        for (Vertex vertex = target; vertex != null; vertex = vertex.previous){
        	path.add(vertex);
        }
        Collections.reverse(path);
        return path;
    }
    
    /** Computes the vertexes from the file passed */
    public static void computeVertexes(File f) throws FileNotFoundException{
    	Scanner fileScn = new Scanner(f);	// Scanner for the file
    	Scanner lineScn;
    	// First find all the nodes we are going to be using
    	while(fileScn.hasNextLine()){
    		lineScn = new Scanner(fileScn.nextLine());
    		Vertex v = new Vertex(lineScn.next());
    		vertices.add(v);
    	}
    	
    	/**Now go through each vertex we found and add their adjacencies**/
    	fileScn = new Scanner(f);	//Restart the file scanner
    	while(fileScn.hasNextLine()){
    		lineScn = new Scanner(fileScn.nextLine());	// Scanner for the specific line
    		
    		Vertex startVertex = new Vertex(lineScn.next());	//Create the first vertex
    		startVertex = findVertex(startVertex);				//Find the ACTUAL vertex in the list already
    		/**note, should probably use an iterator through the vertices list if I figure out how to use them **/
    		
    		while(lineScn.hasNext()){
    			Vertex nextVertex = new Vertex(lineScn.next());
    			int weight = lineScn.nextInt();
    			
    			//Find the vertex in vertices to make sure we are linking the vertexes to each other instead of creating new ones
    			nextVertex = findVertex(nextVertex);
    			
    			//Add the new adjacency
    			startVertex.adjacencies.add(new Edge(nextVertex,weight));
    		}
    	}
    }
    
    /** Checks to see if the passed vertex exists in the vertices list*/
    public static Vertex findVertex(Vertex v){
    	for(int i=0; i<vertices.size(); i++)
			if(vertices.get(i).name.equals(v.name))
				return vertices.get(i);
		
    	return v;
    	
    }

    /** Prints the information of the passed list*/
    public static void printRouteInfo(List<Vertex> list){
    	
		int price = 0;
		for(int c=0; c<list.size(); c++){
			price += list.get(c).minDistance;
		}
    	
		System.out.println("Price:        " + price);
		System.out.println("Connections   " + (list.size()-2));
		System.out.print("Route:        ");
		for(int d=0; d<list.size();d++){
			if(d != list.size()-1)
				System.out.print(list.get(d).name + " -> ");
			else
				System.out.println(list.get(d).name);
		}
    }
    
    public static void main(String[] args) throws FileNotFoundException
    {
    	Scanner input = new Scanner(System.in);	//Create a scanner to read the files
    	File file = new File("airports.txt");	//File to be read from
    	computeVertexes(file);	//Find out all the vertexes and edges from the file
    	
    	Vertex departure;	//Departure vertex to find
    	Vertex arrival;		//Arrival vertex to find
    	String cont = "";	//User input for continuing the program
    	
    	do{
	    	System.out.print("Enter departure airport: ");
	    	departure = new Vertex(input.next());
	    	
	    	if(findVertex(departure) == departure)	//If the airport inputed does not exist, tell user
	    		System.out.println("That airport does not exist");
	    	else{	//Otherwise, continue
		    	departure = findVertex(departure);
		    	
		    	System.out.print("Enter arrival airport: ");
		    	arrival = new Vertex(input.next());
		    	
		    	if(findVertex(arrival) == arrival)	//If the airport inputed does not exist, tell user
		    		System.out.println("That airport does not exist");
		    	else{	//Otherwise continue
		    		arrival = findVertex(arrival);
		    		
			    	if(departure.equals(arrival))	//If the user inputed the same airport code for the departure and arrival
			    		System.out.println("You are already at that airport");
			    	else{
			    		computePaths(departure);	//Otherwise, compute the paths with destination as the source
			    		List<Vertex> lst = getShortestPathTo(arrival);
			    		printRouteInfo(lst);	//Print the info from the list returned by "getShortestPathTo"
			    	}
			    }
		    }
	    	
	    	System.out.print("Check another route? (Y/N)? ");
	    	cont = input.next();
	    	
    	}while(cont.equalsIgnoreCase("Y"));
    	
        	
    }
}