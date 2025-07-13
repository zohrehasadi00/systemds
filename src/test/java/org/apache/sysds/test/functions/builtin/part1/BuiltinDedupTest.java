// File: BuiltinDedupTest.java
// JUnit test with deterministic embeddings and real assertions

package org.apache.sysds.test.functions.builtin.part1;

import org.apache.sysds.common.Types.ExecType;
import org.apache.sysds.common.Types.ValueType;
import org.apache.sysds.runtime.frame.data.FrameBlock;
import org.apache.sysds.runtime.meta.MatrixCharacteristics;
import org.apache.sysds.test.AutomatedTestBase;
import org.apache.sysds.test.TestConfiguration;
import org.apache.sysds.test.TestUtils;
import org.junit.Assert;
import org.junit.Test;

public class BuiltinDedupTest extends AutomatedTestBase {
    private static final String TEST_NAME = "distributed_representation";
    private static final String TEST_DIR  = "functions/builtin/";
    private static final String TEST_CLASS_DIR = TEST_DIR + "BuiltinDedupTest/";

    @Override
    public void setUp() {
        clearAssertionInformation();
        addTestConfiguration(TEST_NAME,
            new TestConfiguration(TEST_CLASS_DIR, TEST_NAME, new String[]{"Y"}));
        if (TEST_CACHE_ENABLED)
            setOutAndExpectedDeletionDisabled(true);
    }

    @Test
    public void testSimpleDedupCP() {
        runTestCase(ExecType.CP);
    }

    private void runTestCase(ExecType execType) {
        ExecType old = setExecMode(execType);
        try {
            loadTestConfiguration(getTestConfiguration(TEST_NAME));
            String HOME = SCRIPT_DIR + TEST_DIR;
            fullDMLScriptName = HOME + TEST_NAME + ".dml";

            programArgs = new String[]{
                "-stats", "-args",
                input("X"), input("gloveMatrix"), input("vocab"),
                "cosine", "0.8", output("Y")
            };

            // --- X frame ---
            String[][] X = {
                {"John Doe",  "New York"},
                {"Jon Doe",   "New York City"},
                {"Jane Doe",  "Boston"},
                {"John Doe",  "NY"}
            };
            ValueType[] schemaX = {
                ValueType.STRING, ValueType.STRING
            };
            FrameBlock fbX = new FrameBlock(schemaX);
            for (String[] row : X) fbX.appendRow(row);
            writeInputFrameWithMTD("X", fbX,
                true,
                new MatrixCharacteristics(X.length, X[0].length, -1, -1),
                schemaX, FileFormat.BINARY);

            // --- Vocab frame ---
            String[][] vocab = {
                {"john"}, {"doe"}, {"new"}, {"york"},
                {"city"}, {"boston"}, {"ny"},  {"jane"}
            };
            ValueType[] schemaV = { ValueType.STRING };
            FrameBlock fbV = new FrameBlock(schemaV);
            for (String[] row : vocab) fbV.appendRow(row);
            writeInputFrameWithMTD("vocab", fbV,
                true,
                new MatrixCharacteristics(vocab.length, 1, -1, -1),
                schemaV, FileFormat.BINARY);

            // --- Deterministic gloveMatrix: small fixed values ---
            double[][] glove = {
                // john, doe, new,  york, city, boston, ny,   jane  (50-dim each; we use 3 dims here for brevity)
                {1,0,0}, {1,0,0}, {0,1,0}, {0,1,0},
                {0,0,1}, {0,0,1}, {0,1,0}, {1,0,0}
            };
            writeInputMatrixWithMTD("gloveMatrix", glove, true);

            // Run and read back the result
            runTest(true, false, null, -1);

            // Load the output frame Y
            FrameBlock result = readOutputFrame("Y");
            // Expect: first and third rows kept; second and fourth dropped as duplicates
            String[][] expected = {
                {"John Doe", "New York"},
                {"Jane Doe", "Boston"}
            };
            TestUtils.compareFrames(expected, result);

        } finally {
            rtplatform = old;
        }
    }
}
