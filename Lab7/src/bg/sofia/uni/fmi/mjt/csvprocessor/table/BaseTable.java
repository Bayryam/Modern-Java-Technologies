package bg.sofia.uni.fmi.mjt.csvprocessor.table;

import bg.sofia.uni.fmi.mjt.csvprocessor.exceptions.CsvDataNotCorrectException;
import bg.sofia.uni.fmi.mjt.csvprocessor.table.column.BaseColumn;
import bg.sofia.uni.fmi.mjt.csvprocessor.table.column.Column;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class BaseTable implements Table {

    Map<String, Column> columns = new LinkedHashMap<>();

    @Override
    public void addData(String[] data) throws CsvDataNotCorrectException {

        if (data == null) {
            throw new IllegalArgumentException("Data variable is null!");
        }

        if (data.length != columns.size() && (!columns.isEmpty())) {
            throw new CsvDataNotCorrectException("Data argument length is not the same as columns count!");
        }

        if (columns.isEmpty()) {
            for (String header : data) {
                Column toAdd = new BaseColumn();
                columns.put(header, toAdd);
            }
        } else {
            int iter = 0;
            for (Column column : columns.values()) {
                column.addData(data[iter++]);
            }
        }

    }

    @Override
    public Collection<String> getColumnNames() {
        return Collections.unmodifiableCollection(columns.keySet());
    }

    @Override
    public Collection<String> getColumnData(String column) {

        if (column == null) {
            throw new IllegalArgumentException("Column argument is null!");
        }

        if (column.isBlank()) {
            throw new IllegalArgumentException("Column argument is blank!");
        }

        if (!columns.containsKey(column)) {
            throw new IllegalArgumentException("Such column doesn't exist in the table!");
        }

        return columns.get(column).getData();

    }

    @Override
    public int getRowsCount() {
        int res = 0;
        if (!columns.isEmpty()) {
            res = columns.values().iterator().next().getData().size() + 1;
        }
        return res;
    }

}
