package bg.sofia.uni.fmi.mjt.csvprocessor;

import bg.sofia.uni.fmi.mjt.csvprocessor.exceptions.CsvDataNotCorrectException;
import bg.sofia.uni.fmi.mjt.csvprocessor.table.Table;
import bg.sofia.uni.fmi.mjt.csvprocessor.table.printer.ColumnAlignment;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CsvProcessorTest {

    Table tableMock = mock();
    String expected = "";
    String file = """
            hdr,testheader,z
            testcolumn,b,c
            """;
    String invalidFile = """
            hdr,hdr,z
            hdr,hdr,c
            """;

    String expectedOfFile = """
            | hdr        | testheader | z   |\r
            | ---------: | :--------- | :-: |\r
            | testcolumn | b          | c   |""";

    @Test
    void testReadCsvValidInput() {
        try (Reader testReader = new StringReader(file)) {
            CsvProcessor processor = new CsvProcessor();
            assertDoesNotThrow(() -> processor.readCsv(testReader, ","));
        } catch (IOException exception) {
            //code
        }
    }

    @Test
    void testReadCsvInvalidInput() {
        try (Reader testReader = new StringReader(invalidFile)) {
            CsvProcessor processor = new CsvProcessor();
            assertThrows(CsvDataNotCorrectException.class,() -> processor.readCsv(testReader, ","));
        } catch (IOException exception) {
            //code
        }
    }

    @Test
    void testWriteCsvValidInput() {
        try (Writer testWriter = new StringWriter()) {
            when(tableMock.getColumnNames()).thenReturn(List.of("hdr", "testheader", "z"));
            when(tableMock.getRowsCount()).thenReturn(2);
            when(tableMock.getColumnData("hdr")).thenReturn(List.of("testcolumn"));
            when(tableMock.getColumnData("testheader")).thenReturn(List.of("b"));
            when(tableMock.getColumnData("z")).thenReturn(List.of("c"));
            CsvProcessor processor = new CsvProcessor(tableMock);
            processor.writeTable(testWriter, ColumnAlignment.RIGHT, ColumnAlignment.LEFT, ColumnAlignment.CENTER);
            assertEquals(expectedOfFile, testWriter.toString());
        } catch (IOException exception) {
            //code
        }
    }

}