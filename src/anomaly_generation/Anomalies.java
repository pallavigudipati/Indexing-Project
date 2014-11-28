package anomaly_generation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import similarity.Similarity;

public class Anomalies {

	public static void main(String args[]) {
		generateAnomalousGraph();
	}

	public static void generateAnomalousGraph() {
		Graph graph = new Graph("data/web-Stanford.txt", 281903, 2312497);
		// generateExchangedConnections(0.8, graph);
		// generateMissingVertices(0.8, graph);
		// generateExchangedVertices(0.8, graph);
		// generateMissingConnections(0.2, graph);
		
		String values[] = new String[] { "0.01", "0.05", "0.1", "0.5",
		"0.8" };
		for(int i=0;i<values.length;i++){	
		generateMissingConnections2(	Double.parseDouble(values[i]), graph);
		Utils.writeHashMap(graph.graphList,
				"data/missing_connections_random_"+values[i]+".txt");
		}
		
	}

	public static void generateMissingVertices(double prob, Graph graph) {
		int numMissingVertices = (int) (prob * graph.numNodes);
		List<Integer> missingVertices = new ArrayList<Integer>();
		Utils.fillRandomNumbers(1, graph.numNodes, numMissingVertices,
				missingVertices);

		// Removing vertices' entry. Can't iterate over the HashMap directly.
		HashMap<Integer, List<Integer>> graphList = graph.graphList;
		for (int i = 0; i < missingVertices.size(); ++i) {
			int removeVertex = missingVertices.get(i);
			if (graphList.containsKey(removeVertex)) {
				graphList.remove(removeVertex);
			}
		}

		// Removing entries in other vertices' lists.
		for (Entry<Integer, List<Integer>> entry : graphList.entrySet()) {
			List<Integer> successors = entry.getValue();
			for (int i = 0; i < successors.size(); ++i) {
				if (missingVertices.contains(successors.get(i))) {
					successors.remove(i);
				}
			}
		}
	}

	public static void generateExchangedVertices(double prob, Graph graph) {
		Similarity h = new Similarity();
		DirectedGraph<String, DefaultEdge> pageRankGraph = h
				.readGraph("data/web-Stanford.txt");
		HashMap<Integer, Integer> ranking = h.rankReverse(h
				.PageRankTopology(pageRankGraph));

		int numExchangedVertices = (int) (prob * graph.numNodes / 2); // TODO:
																		// verify?
		// List<Integer> allVertices = new ArrayList<Integer>();
		// Utils.fillRandomNumbers(1, graph.numNodes, 2 * numExchangedVertices,
		// allVertices);
		// List<Integer> oldVertices = allVertices.subList(0,
		// numExchangedVertices);
		// List<Integer> newVertices = allVertices.subList(numExchangedVertices,
		// 2 * numExchangedVertices);
		List<Integer> oldVertices = new ArrayList<Integer>();
		List<Integer> newVertices = new ArrayList<Integer>();
		// int numVertices = graph.numNodes;

		for (int i = 0; i < numExchangedVertices; i = i + 2) {
			int oldVertex = ranking.get(i + 1);
			int newVertex = ranking.get(i + 2);
			oldVertices.add(oldVertex);
			newVertices.add(newVertex);
		}

		// Removing vertices' entry. Can't iterate over the HashMap directly.
		HashMap<Integer, List<Integer>> graphList = graph.graphList;
		for (int i = 0; i < oldVertices.size(); ++i) {
			int oldVertex = oldVertices.get(i);
			int newVertex = newVertices.get(i);
			List<Integer> oldVertexLinks = graphList.get(oldVertex);
			List<Integer> newVertexLinks = graphList.get(newVertex);
			graphList.put(newVertex, oldVertexLinks);
			graphList.put(oldVertex, newVertexLinks);
		}

		// Removing entries in other vertices' lists.
		for (Entry<Integer, List<Integer>> entry : graphList.entrySet()) {
			List<Integer> successors = entry.getValue();
			int successorsSize = successors == null ? 0 : successors.size();
			for (int i = 0; i < successorsSize; ++i) {
				int position = 0;
				if ((position = oldVertices.indexOf(successors.get(i))) != -1) {
					successors.set(i, newVertices.get(position));
				} else if ((position = newVertices.indexOf(successors.get(i))) != -1) {
					successors.set(i, oldVertices.get(position));
				}
			}
		}
	}

	public static void generateExchangedConnections(double prob, Graph graph) {
		// int numEdges = graph.numEdges;
		int numNodes = graph.numNodes;
		HashMap<Integer, List<Integer>> graphList = graph.graphList;
		// int numExchangedEdges = (int) (prob * numEdges);
		int numExchangedEdges = (int) (prob * numNodes);
		int i = 0;
		while (i < numExchangedEdges) {
			int fromVertex = Utils.randomNumber(1, numNodes);
			// No out-going link, continue.
			if (!graphList.containsKey(fromVertex)) {
				continue;
			}
			List<Integer> toVertices = graphList.get(fromVertex);
			// Select an existing out-going edge.
			int toVertexIndex = Utils.randomNumber(0, toVertices.size() - 1);
			toVertices.remove(toVertexIndex);
			// Remove the node from list if out-degree = 0
			if (toVertices.size() == 0) {
				graphList.remove(fromVertex);
			}
			// Select a new from vertex for this edge.
			int newFromVertex = Utils.randomNumber(1, numNodes);
			List<Integer> newToVertices = null;
			if (graphList.containsKey(newFromVertex)) {
				newToVertices = graphList.get(newFromVertex);
			} else {
				newToVertices = new ArrayList<Integer>();
				graphList.put(newFromVertex, newToVertices);
			}

			// Select a new to vertex for this edge.
			int newToVertex = -1;
			while (true) {
				newToVertex = Utils.randomNumber(1, numNodes);
				if (newToVertex != newFromVertex
						&& !newToVertices.contains(newToVertex)) {
					// If we manage to find a new vertex that is not already
					// connected to it.
					// TODO: Currently assuming no vertex is connected to all
					// vertices.
					newToVertices.add(newToVertex);
					break;
				}
			}
			i++;
		}
	}

	public static void generateMissingConnections2(double prob, Graph graph) {
		int numNodes = graph.numNodes;
		int numEdges = graph.numEdges;
		HashMap<Integer, List<Integer>> graphList = graph.graphList;
		int numExchangedEdges = (int) (prob * numNodes);
		//int numExchangedEdges = (int) (prob * numEdges);
		int i = 0;
		while (i < numExchangedEdges) {
			int fromVertex = Utils.randomNumber(1, numNodes);
			// No out-going link, continue.
			if (!graphList.containsKey(fromVertex)) {
				continue;
			}
			List<Integer> toVertices = graphList.get(fromVertex);
			if (toVertices != null && toVertices.size() != 0) {
				int toVertexIndex = Utils
						.randomNumber(0, toVertices.size() - 1);
	
				 
					toVertices.remove(toVertexIndex);
				    i++;
				
			}
		}
	}

	public static void generateMissingConnections(double prob, Graph graph) {
		// int numEdges = graph.numEdges;
		int numNodes = graph.numNodes;
		HashMap<Integer, List<Integer>> graphList = graph.graphList;
		// int numExchangedEdges = (int) (prob * numEdges);
		int numMissingEdges = (int) (prob * numNodes / 2);

		Similarity h = new Similarity();
		DirectedGraph<String, DefaultEdge> pageRankGraph = h
				.readGraph("data/web-Stanford.txt");
		HashMap<Integer, Integer> ranking = h.rankReverse(h
				.PageRankTopology(pageRankGraph));

		int i = 0;
		int numChanged = 0;
		while (numChanged < numMissingEdges) {
			// int fromVertex = Utils.randomNumber(1, numNodes);
			int fromVertex = ranking.get(i + 1);
			List<Integer> fromVertexNeighbors = graphList.get(fromVertex);
			if (fromVertexNeighbors != null) {
				for (int j = i + 1; j < numNodes; ++j) {
					int toVertex = ranking.get(j + 1);
					int index = -1;
					if ((index = fromVertexNeighbors.indexOf(toVertex)) != -1) {
						fromVertexNeighbors.remove(index);
						numChanged++;
						break;
					}
				}
			}
			i++;
		}
	}
}
