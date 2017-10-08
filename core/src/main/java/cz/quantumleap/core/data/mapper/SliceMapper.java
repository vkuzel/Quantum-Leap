package cz.quantumleap.core.data.mapper;

import cz.quantumleap.core.data.LookupDaoManager;
import cz.quantumleap.core.data.transport.Slice;
import cz.quantumleap.core.data.transport.SliceRequest;
import cz.quantumleap.core.data.transport.Table;
import org.jooq.Record;
import org.jooq.RecordHandler;

import java.util.Map;

public class SliceMapper implements RecordHandler<Record> {

    private final SliceRequest sliceRequest;
    private final TableMapper tableMapper;

    private int recordCount = 0;
    private boolean canExtend = false;

    SliceMapper(org.jooq.Table<? extends Record> table, LookupDaoManager lookupDaoManager, SliceRequest sliceRequest) {
        this.sliceRequest = sliceRequest;
        this.tableMapper = new TableMapper(table, lookupDaoManager, sliceRequest.getSort(), sliceRequest.getSize());
    }

    @Override
    public void next(Record record) {
        if (recordCount++ < sliceRequest.getSize()) {
            tableMapper.next(record);
        } else {
            canExtend = true;
        }
    }

    public Slice<Map<Table.Column, Object>> intoSlice() {
        Table<Map<Table.Column, Object>> table = tableMapper.intoTable();
        return new Slice<>(table, sliceRequest, canExtend);
    }
}
