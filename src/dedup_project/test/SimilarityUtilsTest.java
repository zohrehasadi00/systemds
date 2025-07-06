import org.junit.Assert;
import org.junit.Test;

public class similarityUtilsTest {
    @Test
    public void testCosineIdentical(){
        MatrixBlock A = {{1,0}};
        MatrixBlock B = {{1,0}};

        /**
        *MatrixBlock mA = DataConverter.convertToMatrixBlock(A);
        *MatrixBlock mB = DataConverter.convertToMatrixBlock(B);
        */

        double result = SimilarityUtils.cosine(A, B);
        Asssert.assertEquals(1.0, result, 1e-6);
    }
}