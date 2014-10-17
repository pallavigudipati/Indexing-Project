package similarity;

//package org.jgrapht.demo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.*;
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
  	Set<String> v=g1.vertexSet();
    System.out.println("Number of vertices "+ v.size());        

      

      // create a graph based on URL objects
      //DirectedGraph<URL, DefaultEdge> hrefGraph = createHrefGraph();

      // note directed edges are printed as: (<v1>,<v2>)
      //System.out.println(hrefGraph.toString());
  }

  
  /***
   ** Create a graph based by reading from the File.
   **
   ** @return a graph based on String objects.
   */
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
  
  
}
