package similarity;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultEdge;

public class SimHash {
    public static int DIGEST_SIZE = 512;

    
    public MessageDigest messageDigest;

    public static void main(String[] args) {
    	
    	Similarity h = new Similarity();
    	
    	Double values[] = new Double[10];
    	for(int i=0;i<values.length;i++)
    		values[i]=i*0.1;
    	SimHash simhash = new SimHash();
    	DirectedGraph<String, DefaultEdge> g1 = h
				.readGraph("data/web-Stanford.txt");
    	System.out.println("First graph read");
    	HashMap<String, Double> pageRank1 = h.PageRankTopology(g1);
    	System.out.println("Page Rank 1 done");
    	
    	
//    	System.out.println("Starting extracting features 1");
//		List<List<Object>> features1= simhash.weightedFeaturesBaseline(g1, pageRank1);
//		boolean[] signature1 = simhash.getSignature(features1);
		
		
		
    	 String filename = "data/missing_connections_random_0.2.txt";

    		System.out.println("FILENAME "+ filename);
    	    	DirectedGraph<String, DefaultEdge> g2 = h
    					.readGraph(filename);
    			
    		
    			System.out.println("Second graph  read");
    			
    			HashMap<String, Double> pageRank2 = h.PageRankTopology(g2);
    			System.out.println("Page Rank 2 done");	
		
		
     for (int i = 0; i < values.length; i++) {
	      
		
		System.out.println("alpha value "+Double.toString(values[i]));
		
//		System.out.println("Starting extracting features 2");
//		List<List<Object>> features2= simhash.weightedFeaturesBaseline(g2, pageRank2);
//		boolean[] signature2 = simhash.getSignature(features2);
//		System.out.println("Similarity Computation start");
//		Double similarity = simhash.signatureSimilarity(signature1, signature2);
//		System.out.println("Sequnce Similarity baseline = "+ similarity);
	
		System.out.println("Starting extracting features 1");
		Double maxPageRank11= h.maxPageRank(pageRank1);
		System.out.println("Max page Rank 1 "+ maxPageRank11);
		List<List<Object>> features11= simhash.newWeightedFeaturesBaseline(g1,pageRank1,values[i],maxPageRank11);
		boolean[] signature11 = simhash.getSignature(features11);
		Double maxPageRank21= h.maxPageRank(pageRank2);
		System.out.println("Max page Rank 2 "+ maxPageRank21);
		System.out.println("Starting extracting features 2");
		List<List<Object>> features21= simhash.newWeightedFeaturesBaseline(g2, pageRank2,values[i]	,maxPageRank21);
		boolean[] signature21 = simhash.getSignature(features21);
		System.out.println("Similarity Computation start");
		Double similarity1 = simhash.signatureSimilarity(signature11, signature21);
		System.out.println("Sequence Similarity1 = "+ similarity1);
		
		
		System.out.println("Starting extracting features 1");
		Double maxPageRank12= h.maxPageRank(pageRank1);
		System.out.println("Max page Rank 1 "+ maxPageRank12);
		List<List<Object>> features12= simhash.newWeightedFeatures2Baseline(g1,pageRank1,values[i],maxPageRank12);
		boolean[] signature12 = simhash.getSignature(features12);
		Double maxPageRank22= h.maxPageRank(pageRank2);
		System.out.println("Max page Rank 2 "+ maxPageRank22);
		System.out.println("Starting extracting features 2");
		List<List<Object>> features22= simhash.newWeightedFeatures2Baseline(g2, pageRank2,values[i],maxPageRank22);
		boolean[] signature22 = simhash.getSignature(features22);
		System.out.println("Similarity Computation start");
		Double similarity2 = simhash.signatureSimilarity(signature12, signature22);
		System.out.println("Sequence Similarity2 = "+ similarity2);
		
     }
		
		
    }
    
    public SimHash(){
    	
    	 try {
             this.messageDigest = MessageDigest.getInstance("SHA-512");
         } catch (Exception e) {
             System.out.println(e.getMessage());
         }
    	
    }
    

    public static boolean[] byteArrayToBitArray(byte[] bytes) {
        boolean[] bits = new boolean[bytes.length * 8];
        for (int i = 0; i < bytes.length * 8; i++) {
            if ((bytes[i / 8] & (1 << (7 - (i % 8)))) > 0)
                bits[i] = true;
        }
        return bits;
    }

    public boolean[] getSignature(List<List<Object>> features) {
        double[] fingerprintDoubles = new double[DIGEST_SIZE];
        for (List<Object> feature : features) {
        	String featureKey = (String) feature.get(0);
        	double featureValue = (Double) feature.get(1);
            byte[] featureKeyBytes = null;
            try {
                featureKeyBytes = featureKey.getBytes("UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.getMessage();
            }
            // Get the digests for all the features.
            byte[] digest = messageDigest.digest(featureKeyBytes);
            boolean[] digestBits = byteArrayToBitArray(digest);
            for (int j = 0; j < DIGEST_SIZE; ++j) {
                fingerprintDoubles[j] += digestBits[j] ? featureValue : -1 * featureValue;
            }
        }
        boolean[] fingerprint = new boolean[DIGEST_SIZE];
        // If positive, then 1.
        for (int i = 0; i < DIGEST_SIZE; ++i) {
            fingerprint[i] = fingerprintDoubles[i] >= 0.0;
        }
        return fingerprint;
    }
    
    public List<List<Object>> weightedFeaturesBaseline(
			DirectedGraph<String, DefaultEdge> g,
			HashMap<String, Double> pageRank) {
		List<List<Object>> weightFeatures = new ArrayList<List<Object>>();
		Set<String> nodeSet = g.vertexSet();
		for (String s : nodeSet) {
			List<Object> entry = new ArrayList<Object>();
			entry.add(s);
			entry.add(pageRank.get(s));
			weightFeatures.add(entry);
		}

		Set<DefaultEdge> edgeset = g.edgeSet();

		for (DefaultEdge ed1 : edgeset) {
			String v1 = g.getEdgeSource(ed1);
			String v2 = g.getEdgeTarget(ed1);
			List<Object> entry = new ArrayList<Object>();
			String s = v1 + "-" + v2;
			entry.add(s);
			entry.add(pageRank.get(v1)/g.outDegreeOf(v1));
			weightFeatures.add(entry);
		}

		return weightFeatures;
	}
    
    
    public List<List<Object>> newWeightedFeaturesBaseline(
			DirectedGraph<String, DefaultEdge> g,
			HashMap<String, Double> pageRank,Double alpha,Double maxPageRank) {
		List<List<Object>> weightFeatures = new ArrayList<List<Object>>();
		Set<String> nodeSet = g.vertexSet();
		for (String s : nodeSet) {
			List<Object> entry = new ArrayList<Object>();
			entry.add(s);
			entry.add(alpha*(pageRank.get(s))+(1-alpha)*(g.outDegreeOf(s)+0.0));
			weightFeatures.add(entry);
		}

		Set<DefaultEdge> edgeset = g.edgeSet();

		for (DefaultEdge ed1 : edgeset) {
			String v1 = g.getEdgeSource(ed1);
			String v2 = g.getEdgeTarget(ed1);
			List<Object> entry = new ArrayList<Object>();
			String s = v1 + "-" + v2;
			entry.add(s);
			entry.add(pageRank.get(v1)/(g.outDegreeOf(v1)));
			weightFeatures.add(entry);
		}

		return weightFeatures;
	}

    public List<List<Object>> newWeightedFeatures2Baseline(
			DirectedGraph<String, DefaultEdge> g,
			HashMap<String, Double> pageRank,Double alpha,Double maxPageRank) {
		List<List<Object>> weightFeatures = new ArrayList<List<Object>>();
		Set<String> nodeSet = g.vertexSet();
		for (String s : nodeSet) {
			List<Object> entry = new ArrayList<Object>();
			entry.add(s);
		   Set<DefaultEdge> neighbours = g.edgesOf(s);
		   int degree=0;
		   int numberOfNeigbours = neighbours.size();
		   for (DefaultEdge neighbour: neighbours){
			   //String v1 = g.getEdgeSource(ed1);
			String v2 = g.getEdgeTarget(neighbour);
		    degree+= g.outDegreeOf(v2);
			
		}
		   Double averageDegree ;
		   if(numberOfNeigbours!=0)
			   averageDegree = (Double)((degree+0.0)/numberOfNeigbours);
		   else 
			   averageDegree = 0.0;
		entry.add(alpha*(pageRank.get(s))+(1-alpha)*(averageDegree+0.0));
		weightFeatures.add(entry);
		}
		Set<DefaultEdge> edgeset = g.edgeSet();
		
		for (DefaultEdge ed1 : edgeset) {
			String v1 = g.getEdgeSource(ed1);
			String v2 = g.getEdgeTarget(ed1);
			List<Object> entry = new ArrayList<Object>();
			String s = v1 + "-" + v2;
			
			entry.add(s);
			entry.add(pageRank.get(v1)/(g.outDegreeOf(v1)));
			weightFeatures.add(entry);
			
			
		}

		return weightFeatures;
	}
	
	
	
	
	
	public Double signatureSimilarity(boolean[] sequence1, boolean[] sequence2){
		int intersection=0;
		for(int i=0;i<sequence1.length;i++)
			intersection = (sequence1[i]==sequence2[i])?(intersection+1):intersection ;
			System.out.println("Intersection "+ intersection +" length "+ sequence1.length );
		return (Double)((intersection+ 0.0)/sequence1.length);
	}
	
}