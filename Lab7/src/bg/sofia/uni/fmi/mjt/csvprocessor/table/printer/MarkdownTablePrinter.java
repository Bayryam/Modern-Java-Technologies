package bg.sofia.uni.fmi.mjt.csvprocessor.table.printer;

import bg.sofia.uni.fmi.mjt.csvprocessor.table.Table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MarkdownTablePrinter implements TablePrinter {

    private static final String DELIMITER = "|";
    private static final String EMPTY_STRING = "";
    private static final int MIN_WIDTH = 3;
    private final Map<String, Integer> widths = new HashMap<>();

    private String getNthValueOf(String columnName, Table table, int n) {
        Collection<String> values = table.getColumnData(columnName);
        int iter = 0;
        for (String val : values) {
            if (iter == n) {
                return val;
            }
            iter++;
        }
        return EMPTY_STRING;
    }

    private void setWidths(Table table, ColumnAlignment... alignments) {
        Collection<String> columnNames = table.getColumnNames();
        int iter = 0;
        for (String columnName : columnNames) {

            int width = getColumnWidth(table, columnName);
            if (iter < alignments.length &&
                    width < alignments[iter].getAlignmentCharactersCount() + 1) {
                width = alignments[iter].getAlignmentCharactersCount() + 1;
            }
            iter++;

            widths.put(columnName, width);
        }
    }

    private void addFirstRow(Collection<String> rows, Collection<String> columnNames) {
        StringBuilder sb = new StringBuilder();
        int iter = 0;
        for (String columnName : columnNames) {
            sb.append(DELIMITER);
            sb.append(" ");
            sb.append(columnName);
            sb.append(" ".repeat(widths.get(columnName) - columnName.length()));
            sb.append(" ");

        }
        sb.append(DELIMITER);
        rows.add(sb.toString());
    }

    @Override
    public Collection<String> printTable(Table table, ColumnAlignment... alignments) {

        setWidths(table, alignments);
        Collection<String> rows = new ArrayList<>();
        Collection<String> columnNames = table.getColumnNames();
        int rowsCount = table.getRowsCount() - 1;
        addFirstRow(rows, columnNames);
        addAlignmentsRow(rows, table, alignments);

        for (int row = 0; row < rowsCount; row++) {

            StringBuilder sb = new StringBuilder();

            for (String columnName : columnNames) {
                sb.append(DELIMITER);
                sb.append(" ");
                String value = getNthValueOf(columnName, table, row);
                String spacesAtEnd = " ".repeat(widths.get(columnName) - value.length());
                sb.append(value);
                sb.append(spacesAtEnd);
                sb.append(" ");
            }
            sb.append(DELIMITER);
            rows.add(sb.toString());

        }
        return rows;
    }

    private void addAlignmentsRow(Collection<String> rows, Table table, ColumnAlignment... alignments) {
        StringBuilder sb = new StringBuilder();
        int iter = 0;
        for (String columnName : table.getColumnNames()) {
            sb.append(DELIMITER);
            sb.append(" ");
            String value;
            if (iter < alignments.length) {
                value = getValueForAlignmentRow(alignments[iter++], columnName);
            } else {
                value = "-".repeat(widths.get(columnName));
            }
            sb.append(value);
            sb.append(" ");

        }
        sb.append(DELIMITER);
        rows.add(sb.toString());
    }

    private String getValueForAlignmentRow(ColumnAlignment alignment, String columnName) {
        StringBuilder sb = new StringBuilder();
        int width = widths.get(columnName);
        switch (alignment) {
            case ColumnAlignment.RIGHT -> {
                sb.append("-".repeat(width - 1));
                sb.append(":");
            }
            case ColumnAlignment.LEFT -> {
                sb.append(":");
                sb.append("-".repeat(width - 1));
            }
            case ColumnAlignment.CENTER -> {
                sb.append(":");
                sb.append("-".repeat(width - 2));
                sb.append(":");
            }
            case ColumnAlignment.NOALIGNMENT -> sb.append("-".repeat(width));
        }
        return sb.toString();
    }

    private int getColumnWidth(Table table, String columnName) {
        int maxValLength = columnName.length();
        for (String val : table.getColumnData(columnName)) {
            if (maxValLength < val.length()) {
                maxValLength = val.length();
            }
        }

        return Math.max(MIN_WIDTH, maxValLength);
    }
}

