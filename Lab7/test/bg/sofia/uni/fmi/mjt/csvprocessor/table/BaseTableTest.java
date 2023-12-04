package bg.sofia.uni.fmi.mjt.csvprocessor.table;

import bg.sofia.uni.fmi.mjt.csvprocessor.table.column.BaseColumn;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BaseTableTest {

    @Test
    void testAddDataNullArgument() {
        BaseTable table = new BaseTable();
        assertThrows(IllegalArgumentException.class,() -> table.addData(null));
    }

    @Test
    void testGetColumnNames() {
        BaseTable table = new BaseTable();
        table.columns.put("first", new BaseColumn(Set.of("1")));
        table.columns.put("second", new BaseColumn(Set.of("2")));
        table.columns.put("third", new BaseColumn(Set.of("3")));
        Collection<String> data = table.getColumnNames();
        assertThrows(UnsupportedOperationException.class, () -> data.add("test"));
    }

    @Test
    void testGetColumnDataNullArgument() {
        BaseTable table = new BaseTable();
        assertThrows(IllegalArgumentException.class, () -> table.getColumnData(null));
    }

    @Test
    void testGetColumnDataBlankArgument() {
        BaseTable table = new BaseTable();
        assertThrows(IllegalArgumentException.class, () -> table.getColumnData(""));
    }

    @Test
    void testGetColumnDataDontContainKey() {
        BaseTable table = new BaseTable();
        assertThrows(IllegalArgumentException.class, () -> table.getColumnData("test"));
    }

    @Test
    void testGetColumnDataDoesntContainKey() {
        BaseTable table = new BaseTable();
        table.columns.put("test", new BaseColumn(Set.of("result")));
        assertDoesNotThrow(() -> table.getColumnData("test"));
    }
}
