package cz.quantumleap.core.data.transport;

import cz.quantumleap.core.data.transport.Table.Column;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.List;

public class Slice<ROW> implements Iterable<ROW>  {

    private final Table<ROW> table;
    private final SliceRequest sliceRequest;
    private final boolean canExtend;

    public Slice(Table<ROW> table, SliceRequest sliceRequest, boolean canExtend) {
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

    public List<ROW> getRows() {
        return table.getRows();
    }

    @NotNull
    @Override
    public Iterator<ROW> iterator() {
        return table.iterator();
    }

    public boolean isEmpty() {
        return table.isEmpty();
    }

    public boolean canExtend() {
        return canExtend;
    }

    public SliceRequest extend() {
        return canExtend ? sliceRequest.extend() : null;
    }
}
