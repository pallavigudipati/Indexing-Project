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
    public static int DIGEST_SIZE = 128;

    
    public MessageDigest messageDigest;

    public static void main(String[] args) {
    	
    	Similarity h = new Similarity();
    	DirectedGraph<String, DefaultEdge> g2 = h
				.readGraph("data/missing_vertices_0.2.txt");
		DirectedGraph<String, DefaultEdge> g1 = h
				.readGraph("data/web-Stanford.txt");
	

		HashMap<String, Double> pageRank1 = h.PageRankTopology(g1);
		HashMap<String, Double> pageRank2 = h.PageRankTopology(g2);
		SimHash simhash = new SimHash();
		HashMap<String,Double> features1= simhash.weightedFeaturesBaseline(g1, pageRank1);
		boolean[] signature1 = simhash.getSignature(features1);
		HashMap<String,Double> features2= simhash.weightedFeaturesBaseline(g2, pageRank2);
		boolean[] signature2 = simhash.getSignature(features2);
		Double similarity = simhash.signatureSimilarity(signature1, signature2);
		System.out.println("Similarity = "+ similarity);
		

    }
    public SimHash(){
    	
    	 try {
             this.messageDigest = MessageDigest.getInstance("MD5");
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

    public boolean[] getSignature(HashMap<String,Double> features) {
        Object[] featureKeys = features.keySet().toArray();
        double[] fingerprintDoubles = new double[DIGEST_SIZE];
        List<byte[]> digests = new ArrayList<byte[]>();
        for (int i = 0; i < featureKeys.length; ++i) {
            byte[] featureKeyBytes = null;
            double featureValue = features.get(featureKeys[i]);
            try {
                featureKeyBytes = ((String) featureKeys[i]).getBytes("UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.getMessage();
            }
            // Get the digests for all the features.
            byte[] digest = messageDigest.digest(featureKeyBytes);
            boolean[] digestBits = byteArrayToBitArray(digest);
            for (int j = 0; j < DIGEST_SIZE; ++j) {
                fingerprintDoubles[j] += digestBits[i] ? featureValue : -1 * featureValue;
            }
        }
        boolean[] fingerprint = new boolean[DIGEST_SIZE];
        // If positive, then 1.
        for (int i = 0; i < DIGEST_SIZE; ++i) {
            fingerprint[i] = fingerprintDoubles[i] >= 0.0;
        }
        return fingerprint;
    }
    
    public HashMap<String, Double> weightedFeaturesBaseline(
			DirectedGraph<String, DefaultEdge> g,
			HashMap<String, Double> pageRank) {
		HashMap<String, Double> weightFeatures = new HashMap<String, Double>();
		Set<String> nodeSet = g.vertexSet();
		for (String s : nodeSet) {

			weightFeatures.put(s, pageRank.get(s));
		}

		Set<DefaultEdge> edgeset = g.edgeSet();

		for (DefaultEdge ed1 : edgeset) {
			String v1 = g.getEdgeSource(ed1);
			String v2 = g.getEdgeTarget(ed1);

			weightFeatures.put(v1 + "-" + v2,
					pageRank.get(v1) / g.outDegreeOf(v1));
		}

		return weightFeatures;
	}
	
	public HashMap<String, Double> weightedFeaturesNew(
			DirectedGraph<String, DefaultEdge> g,
			HashMap<String, Double> pageRank, Double alpha) {
		HashMap<String, Double> weightFeatures = new HashMap<String, Double>();
		Set<String> nodeSet = g.vertexSet();
		for (String s : nodeSet) {

			weightFeatures.put(s, alpha*pageRank.get(s)+(1-alpha)*g.outDegreeOf(s));
		}

		Set<DefaultEdge> edgeset = g.edgeSet();

		for (DefaultEdge ed1 : edgeset) {
			String v1 = g.getEdgeSource(ed1);
			String v2 = g.getEdgeTarget(ed1);

			weightFeatures.put(v1 + "-" + v2,
					pageRank.get(v1) / g.outDegreeOf(v1));
		}

		return weightFeatures;
	}
	
	
	
	public Double signatureSimilarity(boolean[] sequence1, boolean[] sequence2){
		int intersection=0;
		for(int i=0;i<sequence1.length;i++)
			intersection = (sequence1[i]==sequence2[i])?(intersection+1):intersection ;
		return (Double)(intersection+ 0.0/sequence1.length);
	}
	
}