package org.apache.sysds.runtime.functions.dedup;
import org.apache.sysds.runtime.matrix.data.MatrixBlock;
import org.apache.sysds.runtime.util.UtilFunctions;
import java.util.*;
import java.util.stream.IntStream;



/**
 * Performs Locality Sensitive Hashing (LSH) based blocking
 * using random hyperplanes for cosine similarity.
 */
public class LSHBlocker {

    /**
     * Creates LSH buckets for the dataset.
     *
     * @param dataset MatrixBlock input
     * @param numHashes Number of hash functions
     * @return Map of hash codes to row indices
     */
    
    public static Map<String, List<Integer>> block(MatrixBlock dataset, int numHashes) 
    { 
        Random rand = new Random(42);
        int dims = dataset.getNumColumns();
        double[][] hyperplanes = new double[numHashes][dims];

        // Generate random hyperplanes
        for (int h = 0; h < numHashes; h++)
            for (int d = 0; d < dims; d++)
                hyperplanes[h][d] = rand.nextGaussian();

        Map<String, List<Integer>> buckets = new HashMap<>();

        // Hash each row
        for (int i = 0; i < dataset.getNumRows(); i++) {
            StringBuilder hashCode = new StringBuilder();
            for (int h = 0; h < numHashes; h++) {
                double dot = 0;
                for (int d = 0; d < dims; d++)
                    dot += dataset.quickGetValue(i, d) * hyperplanes[h][d];
                hashCode.append(dot >= 0 ? "1" : "0");
            }
            buckets.computeIfAbsent(hashCode.toString(), k -> new ArrayList<>()).add(i);
        }

        return buckets;
    }
}
