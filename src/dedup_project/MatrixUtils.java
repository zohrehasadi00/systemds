package org.apache.sysds.runtime.functions.dedup;
import org.apache.sysds.runtime.matrix.data.MatrixBlock;
import org.apache.sysds.runtime.util.UtilFunctions;
import java.util.*;
import java.util.stream.IntStream;

public class MatrixUtils {

        /**
     * MatrixUtils contains utility methods for filtering duplicate rows from a matrix.
     * These support two modes:
     * - Removing duplicates to retain only unique rows
     * - Extracting duplicates to return only duplicate rows
     *
     * The set of duplicates is provided as a set of index pairs (i, j), where rows i and j
     * are considered similar based on a thresholded similarity function.
     */


    public static MatrixBlock removeRows(MatrixBlock input, Set<int[]> dups) {
        System.out.println("Removing " + dups.size() + " duplicate rows");
        Set<Integer> dupSet = new HashSet<>();
        for (int[] pair : dups) {
            dupSet.add(pair[0]);
            dupSet.add(pair[1]);
        }

        MatrixBlock result = new MatrixBlock();
        for (int i = 0; i < input.getNumRows(); i++) {
            if (!dupSet.contains(i)) {
                MatrixBlock row = input.sliceOperations(i, i, 0, input.getNumColumns() - 1);
                result.appendRow(result.getNumRows(), row);
            }
        }
        return result;
    }

    public static MatrixBlock extractRows(MatrixBlock input, Set<int[]> dups) {
        System.out.println("Extracting " + dups.size() + " duplicate rows");
        Set<Integer> dupSet = new HashSet<>();
        for (int[] pair : dups) {
            dupSet.add(pair[0]);
            dupSet.add(pair[1]);
        }

        MatrixBlock result = new MatrixBlock();
        for (int i : dupSet) {
            MatrixBlock row = input.sliceOperations(i, i, 0, input.getNumColumns() - 1);
            result.appendRow(result.getNumRows(), row);
        }
        return result;
    }
}
