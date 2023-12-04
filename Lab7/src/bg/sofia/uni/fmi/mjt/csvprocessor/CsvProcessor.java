package bg.sofia.uni.fmi.mjt.csvprocessor;

import bg.sofia.uni.fmi.mjt.csvprocessor.exceptions.CsvDataNotCorrectException;
import bg.sofia.uni.fmi.mjt.csvprocessor.table.BaseTable;
import bg.sofia.uni.fmi.mjt.csvprocessor.table.Table;
import bg.sofia.uni.fmi.mjt.csvprocessor.table.printer.ColumnAlignment;
import bg.sofia.uni.fmi.mjt.csvprocessor.table.printer.MarkdownTablePrinter;
import bg.sofia.uni.fmi.mjt.csvprocessor.table.printer.TablePrinter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Collection;

public class CsvProcessor implements CsvProcessorAPI {

    private final Table table;

    public CsvProcessor() {
        this(new BaseTable());
    }

    public CsvProcessor(Table table) {
        this.table = table;
    }

    @Override
    public void readCsv(Reader reader, String delimiter) throws CsvDataNotCorrectException {

        try (BufferedReader customReader = new BufferedReader(reader)) {
            String line;
            while ((line = customReader.readLine()) != null) {
                String regex = "\\Q" + delimiter + "\\E";
                String[] data = line.split(regex);
                table.addData(data);
            }
        } catch (IOException exception) {
            throw new CsvDataNotCorrectException("Invalid file format!", exception);
        }

    }

    @Override
    public void writeTable(Writer writer, ColumnAlignment... alignments) {
        TablePrinter tablePrinter = new MarkdownTablePrinter();
        Collection<String> rows = tablePrinter.printTable(table, alignments);

        try (BufferedWriter customWriter = new BufferedWriter(writer)) {
            int size = rows.size();
            for (String row : rows) {
                customWriter.write(row);
                if (size != 1) {
                    customWriter.write(System.lineSeparator());
                }
                size--;
            }
        } catch (IOException exception) {
            //some logic
        }
    }
}
