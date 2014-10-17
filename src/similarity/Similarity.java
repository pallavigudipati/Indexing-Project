package similarity;

//package org.jgrapht.demo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.*;
import java.util.Iterator;
import java.util.Set;

import org.jgrapht.*;
import org.jgrapht.graph.*;


public  class Similarity
{

 
  private Similarity()
  {
  } 

  
  /***
   ** The starting point for the demo.
   **
   ** @param args ignored.
   */
  public static void main(String [] args)
  {
  	Similarity h = new Similarity();
  	
  	DirectedGraph<String, DefaultEdge> g1 = h.readGraph("data/web-Stanford.txt");
  	DirectedGraph<String, DefaultEdge> g2 = h.readGraph("data/web-Stanford.txt");
  	g2.removeVertex("1");
  	Set<String> v=g1.vertexSet();
    System.out.println("Number of vertices "+ v.size());        

    System.out.println("Vertex Edge Overlap between the Graphs is  "+ h.vertexEdgeOverlap(g1, g2));

      
  }

  
 
  private  DirectedGraph<String, DefaultEdge> readGraph(String filename)
  {
  	DirectedGraph<String, DefaultEdge> g =
              new DefaultDirectedGraph<String, DefaultEdge>
              (DefaultEdge.class);
  	BufferedReader br = null;
  	
     
  	 
		try {

			String sCurrentLine;

			br = new BufferedReader(new FileReader(filename));

			while ((sCurrentLine = br.readLine()) != null) {
				String[] edge = sCurrentLine.split("\t");
				if(!g.containsVertex(edge[0])){
					g.addVertex(edge[0]);
				}
				if(!g.containsVertex(edge[1])){
					g.addVertex(edge[1]);
				}
				g.addEdge(edge[0],edge[1]);
					
			
				
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

	
  return g;
  }
  
  public double  vertexEdgeOverlap(DirectedGraph<String, DefaultEdge> g1, DirectedGraph<String, DefaultEdge> g2){
	  
	  int n1 = g1.vertexSet().size();
	  int n2 = g2.vertexSet().size();
	  long e1 = g1.edgeSet().size();
	  long e2 = g2.edgeSet().size();
	  long commonEdges =0;
	  int commonVertices = 0;
	  double vertexEdgeOverlap=0.0;
	
	  Set<String>  nodes  = g1.vertexSet();
	  for (String s: nodes){
		  if(g2.containsVertex(s))
			  commonVertices++;
	  }
	  
	  
	 
	  Set<DefaultEdge> edgeset1 =g1.edgeSet();
	  for(DefaultEdge e : edgeset1){
		  System.out.println(e.toString());
		  if(g2.containsEdge(e)){
			  commonEdges++;
		  }
	  }
	  
	  
	  
	   
	   vertexEdgeOverlap = 2*(double) (commonVertices + commonEdges)/(n1+n2+ e1 + e2);
	   System.out.println("N1 = "+ n1 + " N2 "+n2 +" E1 "+ e1 + " E2 "+ e2 + " Common Edges "+ commonEdges + " Common vertices "+ commonVertices +" Answer " + vertexEdgeOverlap);
	   
	  return vertexEdgeOverlap;
	  
  }
  
  
}
