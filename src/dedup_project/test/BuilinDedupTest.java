import org.apache.sysds.runtime.matrix.data.MatrixBlock;
import org.apache.sysds.runtime.matrix.data.DataConverter;
import org.junit.Test;
import static org.junit.Assert.*;

public class BuilinDedupTest {

    @Test
    public void testMatrixBlockCreation() {
        double[][] data = {
            {1.0, 2.0},
            {1.0, 2.0},
            {3.0, 4.0},
            {3.01, 4.01},
            {5.0, 6.0}
        };

        MatrixBlock input = DataConverter.convertToMatrixBlock(data);
        assertEquals(5, input.getNumRows());
        assertEquals(2, input.getNumColumns());
    }
}
