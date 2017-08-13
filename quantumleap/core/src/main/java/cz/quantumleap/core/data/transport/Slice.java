package cz.quantumleap.core.data.transport;

import cz.quantumleap.core.data.transport.Table.Column;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Slice<ROW> implements Iterable<ROW>  {

    private final Table table;
    private final SliceRequest sliceRequest;
    private final boolean canExtend;

    public Slice(Table table, SliceRequest sliceRequest, boolean canExtend) {
        this.table = table;
        this.sliceRequest = sliceRequest;
        this.canExtend = canExtend;
    }

    public String getName() {
        return table.getDatabaseTableNameWithSchema();
    }

    public List<Column> getColumns() {
        return table.getColumns();
    }

    public List<Map<Column, Object>> getRows() {
        return table.getRows();
    }

    @Override
    public Iterator<ROW> iterator() {
        return table.iterator();
    }

    public boolean canExtend() {
        return canExtend;
    }

    public SliceRequest extend() {
        return canExtend ? sliceRequest.extend() : null;
    }

}
