package org.apache.sysds.runtime.functions.dedup;

import org.apache.sysds.runtime.matrix.data.MatrixBlock;
import org.apache.sysds.runtime.util.UtilFunctions;
import java.util.*;
import java.util.stream.IntStream;

/**
 * Builtin function for deduplicating rows in a matrix.
 * Syntax: dedup(matrix X, string similarity, boolean returnDuplicates)
 */
public class BuiltinDedup extends BuiltinUnaryFunction {

    @Override
    public void processInstruction(ExecutionContext ec) {
        // 1. Get inputs from ExecutionContext
        MatrixObject in = ec.getMatrixInput(input1.getName());
        String simType = getScalarInput(similarityParam, String.class, ec);
        boolean returnDups = getScalarInput(returnDuplicatesParam, Boolean.class, ec);

        // 2. Call blocking & detection
        MatrixBlock mb = in.getMatrixBlock();
        boolean useCosine = "cosine".equalsIgnoreCase(simType);
        Map<?, List<Integer>> blocks = useCosine ?
            LSHBlocker.block(mb, 10) : KMeansBlocker.cluster(mb, 5);
        Set<int[]> dups = DuplicateDetector.detect(mb, blocks, 0.95, useCosine);

        // 3. Create result matrix
        MatrixBlock result = returnDups
            ? MatrixUtils.extractRows(mb, dups)
            : MatrixUtils.removeRows(mb, dups);

        // 4. Output
        MatrixObject out = ec.getMatrixOutput(output.getName());
        out.setMatrixBlock(result);
        ec.releaseMatrixInput(input1.getName());

        /* Printing */
        MatrixObject outputMatrixObject = ec.getMatrixObject(output.getName());
        MatrixBlock outputMatrixBlock = outputMatrixObject.acquireReadAndRelease();
        double[][] outputArray = DataConverter.convertToDoubleMatrix(outputMatrixBlock);
        for (int i = 0; i < outputArray.length; i++) {
            for (int j = 0; j < outputArray[i].length; j++) {
                System.out.print(outputArray[i][j] + " ");
            }
            System.out.println();
        }
    }
}
