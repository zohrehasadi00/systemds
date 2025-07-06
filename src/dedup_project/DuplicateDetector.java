package org.apache.sysds.runtime.functions.dedup;

import org.apache.sysds.runtime.matrix.data.MatrixBlock;
import org.apache.sysds.runtime.util.UtilFunctions;
import java.util.*;
import java.util.stream.IntStream;

/**
 * Compares tuple pairs within blocks to detect duplicates.
 */
public class DuplicateDetector {

    /**
     * Returns duplicate row index pairs from blocks.
     *
     * @param dataset MatrixBlock
     * @param blocks Clustered row groups
     * @param threshold Threshold for match
     * @param useCosine True for cosine, false for Euclidean
     * @return Set of duplicate index pairs
     */
    public static Set<int[]> detect(MatrixBlock dataset,
                                    Map<?, List<Integer>> blocks,
                                    double threshold,
                                    boolean useCosine) 
                                    { 
                                        Set<int[]> duplicates = new HashSet<>();
        for (List<Integer> block : blocks.values()) {
            for (int i = 0; i < block.size(); i++) {
                for (int j = i + 1; j < block.size(); j++) {
                    MatrixBlock rowA = dataset.sliceOperations(block.get(i), block.get(i), 0, dataset.getNumColumns() - 1);
                    MatrixBlock rowB = dataset.sliceOperations(block.get(j), block.get(j), 0, dataset.getNumColumns() - 1);
                    double score = useCosine
                            ? cosineSimilarity(rowA, rowB)
                            : euclideanDistance(rowA, rowB);
                    boolean isDuplicate = useCosine
                            ? score >= threshold
                            : score <= threshold;
                    if (isDuplicate)
                        duplicates.add(new int[]{block.get(i), block.get(j)});
                }
            }
        }
        return duplicates;
                                    }
}
