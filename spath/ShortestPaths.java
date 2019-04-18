//
// SHORTESTPATHS.JAVA
// Compute shortest paths in a weighted, directed graph.
//

package spath;

import java.util.LinkedList;
import java.util.HashMap;

// heap-related structures from Lab 3
import heaps.Decreaser;
import heaps.MinHeap;

// directed graph structure
import spath.graphs.DirectedGraph;
import spath.graphs.Edge;
import spath.graphs.Vertex;

// vertex/dist pair for priority queue
import spath.VertexAndDist;

import timing.Ticker;


public class ShortestPaths {

    // "infinity" value for path lengths
    private final static Integer inf = Integer.MAX_VALUE;
    
    // a directed graph, and a weighting function on its edges
    private final DirectedGraph g;
    private HashMap<Edge, Integer> weights;	
    
    // starting vertex for shortest path computation
    private Vertex startVertex;
    
    // map from vertices to their handles into the priority queue
    private HashMap<Vertex, Decreaser<VertexAndDist>> handles;	//ask Min about this, how does the pq work
    
    // map from vertices to their parent edges in the shortest-path tree
    private HashMap<Vertex, Edge> parentEdges;	//ask Min about this
    
    
    //
    // constructor
    //
    public ShortestPaths(DirectedGraph g, HashMap<Edge,Integer> weights, Vertex startVertex) {
    	this.g           = g;
    	this.weights     = weights;

    	this.startVertex = startVertex;	
	
    	this.handles     = new HashMap<Vertex, Decreaser<VertexAndDist>>();
    	this.parentEdges = new HashMap<Vertex, Edge>();
    }

    
    //
    // run() 
    //
    // Given a weighted digraph (directed graph) stored in g/weights, compute a
    // shortest-path tree of parent edges back to a given starting
    // vertex.
    //
    public void run() {
    	Ticker ticker = new Ticker(); // heap requires a ticker
	
    	MinHeap<VertexAndDist> pq = new MinHeap<VertexAndDist>(g.getNumVertices(), ticker);
	
    	//
    	// Put all vertices into the heap, infinitely far from start.
    	// Record handle to each inserted vertex, and initialize
    	// parent edge of each to null (since we have as yet found 
    	// no path to it.)
    	//
    	for (Vertex v : g.vertices()) {
    		Decreaser<VertexAndDist> d = pq.insert(new VertexAndDist(v, inf));
    		handles.put(v, d);
    		parentEdges.put(v, null);
    	}
	
    	//
    	// Relax the starting vertex's distance to 0.
    	//   - get the handle to the vertex from the heap
    	//   - extract the vertex + distance object from the handle
    	//   - create a *new* vertex + distance object with a reduced 
    	//      distance
    	//   - update the heap through the vertex's handle
    	//
    	Decreaser<VertexAndDist> startHandle = handles.get(startVertex);
    	VertexAndDist vd = startHandle.getValue();
    	startHandle.decrease(new VertexAndDist(vd.vertex, 0));
    	
    	
	
    	//
    	// OK, now it's up to you!
    	// Implement the main loop of Dijkstra's shortest-path algorithm,
    	// recording the parent edges of each vertex in parentEdges.
    	// FIXME
    	//																	//priority queue holds VertexAndDist objects
    	while (!pq.isEmpty()) {												//while the priority queue is not empty
    		VertexAndDist foo = pq.extractMin();							//extract the VertexAndDist object, it's now foo
    		
    		for (Edge x : foo.getVertex().edgesFrom()) {					//get ALL edges coming out of that vertex and iterate through them
    			int distance = weights.get(x) + foo.getDistance(); 			//the distance of that edge is weight of most recent edge + old distance
    			
    			Vertex e = x.to;											//find the vertex this out-coming edge is going to
    			Decreaser<VertexAndDist> getDec = handles.get(e);			//e is the key, get the `location` in heap that matches with this key
    				
    			VertexAndDist val = getDec.getValue();						//get the VertexAndDist at this `location`
    			int dist = val.getDistance() ;								//get the distance of this Vertex
    			
    			if(distance < dist) { 										//if new shortest distance is smaller than old distance
    				VertexAndDist v = val;									//make an object and set it equal to the VertexAndDist object at `location`
    				getDec.decrease(new VertexAndDist(v.vertex, distance)); //update the heap b/c of heap invariant since the value stored was changed
    				parentEdges.put(x.to, x);								
    			  //parentEdges.put(next vertex, current edge);// this is because the current edge becomes the previous edge of the next vertex, get it??
    			}
    		}
    	}
    }
    
    
    //
    // returnPath()
    //
    // Given an ending vertex v, compute a linked list containing every
    // edge on a shortest path from the starting vertex (stored) to v.
    // The edges should be ordered starting from the start vertex.
    //
    public LinkedList<Edge> returnPath(Vertex endVertex) {
    	LinkedList<Edge> path = new LinkedList<Edge>();
	
    	//
    	// FIXME: implement this using the parent edges computed in run()
    	//
    	while (endVertex != startVertex) {
    		Edge foo = parentEdges.get(endVertex);
    		path.addFirst(foo);
    		endVertex = foo.from;
    	}
	
    	return path;
    }

    ////////////////////////////////////////////////////////////////
    
    //
    // returnLength()
    // Compute the total weight of a putative shortest path
    // from the start vertex to the specified end vertex.
    // No user-serviceable parts inside.path.addFirst(foo);
    //
    public int returnLength(Vertex endVertex) {
    	LinkedList<Edge> path = returnPath(endVertex);
	
    	int pathLength = 0;
    	for(Edge e : path) {
    		pathLength += weights.get(e);
    	}
	
    	return pathLength;
    }
}
