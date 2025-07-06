package org.apache.sysds.runtime.functions.dedup;
import org.apache.sysds.runtime.matrix.data.MatrixBlock;
import org.apache.sysds.runtime.util.UtilFunctions;
import java.util.*;
import java.util.stream.IntStream;

/**
 * Utility class for similarity computations between rows of a matrix.
 */
public class SimilarityUtils {

    /**
     * Computes the cosine similarity between two rows.
     */
    public static double cosine(MatrixBlock rowA, MatrixBlock rowB) 
    { 
        double dot = 0.0, normA = 0.0, normB = 0.0;
        for (int i = 0; i < rowA.getNumColumns(); i++) {
            double a = rowA.quickGetValue(0, i);
            double b = rowB.quickGetValue(0, i);
            dot += a * b;
            normA += a * a;
            normB += b * b;
        }
        return dot / (Math.sqrt(normA) * Math.sqrt(normB) + 1e-10); 
    }

    /**
     * Computes the Euclidean distance between two rows.
     */
    public static double euclidean(MatrixBlock rowA, MatrixBlock rowB) 
    {
        double sum = 0.0;
        for (int i = 0; i < rowA.getNumColumns(); i++) {
            double diff = rowA.quickGetValue(0, i) - rowB.quickGetValue(0, i);
            sum += diff * diff;
        }
        return Math.sqrt(sum);
    }
}