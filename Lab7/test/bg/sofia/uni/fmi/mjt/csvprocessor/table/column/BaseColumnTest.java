package bg.sofia.uni.fmi.mjt.csvprocessor.table.column;

import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BaseColumnTest {

    @Test
    void testAddDataNullArgument() {
        BaseColumn column = new BaseColumn();
        assertThrows(IllegalArgumentException.class, () -> column.addData(null));
    }

    @Test
    void testAddDataBlankArgument() {
        BaseColumn column = new BaseColumn();
        assertThrows(IllegalArgumentException.class, () -> column.addData(""));
    }

    @Test
    void testGetDataUnsupportedOperationException() {
        BaseColumn column = new BaseColumn();
        Collection<String> data = column.getData();
        assertThrows(UnsupportedOperationException.class, () -> data.add("test"));
    }

    @Test
    void testGetData() {
        BaseColumn column = new BaseColumn(Set.of("test1", "test2", "test3"));
        assertEquals(Set.of("test1", "test2", "test3"), column.getData());
    }
}
