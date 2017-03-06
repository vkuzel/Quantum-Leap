package cz.quantumleap.core.persistence.transport;

import cz.quantumleap.core.persistence.transport.NamedTable.Column;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Slice implements Iterable<Map<String, Object>>  {

    private final NamedTable table;
    private final SliceRequest sliceRequest;
    private final boolean canExtend;

    public Slice(NamedTable table, SliceRequest sliceRequest, boolean canExtend) {
        this.table = table;
        this.sliceRequest = sliceRequest;
        this.canExtend = canExtend;
    }

    public String getName() {
        return table.getName();
    }

    // TODO More like getIdColumns... and move it to table
    public Column getIdColumn() {
        // TODO This is mess!
        return table.getColumns().stream().filter(Column::isIdentifier).findAny().orElse(table.getColumns().get(0));
    }

    public List<Column> getColumns() {
        return table.getColumns().stream().filter(column -> !column.isIdentifier()).collect(Collectors.toList());
    }

    public List<Map<String, Object>> getRows() {
        return table.getRows();
    }

    @Override
    public Iterator<Map<String, Object>> iterator() {
        return table.iterator();
    }

    public boolean canExtend() {
        return canExtend;
    }

    public SliceRequest extend() {
        return canExtend ? sliceRequest.extend() : null;
    }

}
