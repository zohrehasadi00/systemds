package org.apache.sysds.runtime.functions.dedup;

import org.apache.sysds.runtime.matrix.data.MatrixBlock;
import org.apache.sysds.runtime.util.UtilFunctions;
import java.util.*;
import java.util.stream.IntStream;


/**
 * Performs k-means clustering for Euclidean-based blocking.
 */
public class KMeansBlocker {

    /**
     * Clusters rows of the dataset using k-means.
     *
     * @param dataset MatrixBlock input
     * @param k Number of clusters
     * @return Map of cluster IDs to row indices
     */
    public static Map<Integer, List<Integer>> cluster(MatrixBlock dataset, int k) 
    {
        int n = dataset.getNumRows();
        int d = dataset.getNumColumns();
        Random rand = new Random(42);
        double[][] centroids = new double[k][d];

        // Initialize centroids randomly
        for (int c = 0; c < k; c++) {
            int row = rand.nextInt(n);
            for (int j = 0; j < d; j++)
                centroids[c][j] = dataset.quickGetValue(row, j);
        }

        Map<Integer, List<Integer>> clusters = new HashMap<>();
        boolean changed;
        do {
            clusters.clear();
            for (int i = 0; i < n; i++) {
                double[] row = new double[d];
                for (int j = 0; j < d; j++)
                    row[j] = dataset.quickGetValue(i, j);
                int bestCluster = 0;
                double bestDist = Double.MAX_VALUE;
                for (int c = 0; c < k; c++) {
                    double dist = 0.0;
                    for (int j = 0; j < d; j++)
                        dist += Math.pow(row[j] - centroids[c][j], 2);
                    if (dist < bestDist) {
                        bestDist = dist;
                        bestCluster = c;
                    }
                }
                clusters.computeIfAbsent(bestCluster, x -> new ArrayList<>()).add(i);
            }

            // Update centroids
            changed = false;
            for (int c = 0; c < k; c++) {
                double[] newCentroid = new double[d];
                List<Integer> rows = clusters.getOrDefault(c, new ArrayList<>());
                if (rows.isEmpty()) continue;
                for (int r : rows)
                    for (int j = 0; j < d; j++)
                        newCentroid[j] += dataset.quickGetValue(r, j);
                for (int j = 0; j < d; j++)
                    newCentroid[j] /= rows.size();
                for (int j = 0; j < d; j++) {
                    if (centroids[c][j] != newCentroid[j])
                        changed = true;
                    centroids[c][j] = newCentroid[j];
                }
            }
        } while (changed);

        return clusters;
     }
}
