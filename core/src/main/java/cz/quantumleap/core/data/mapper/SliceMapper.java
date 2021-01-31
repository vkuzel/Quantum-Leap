package cz.quantumleap.core.data.mapper;

import cz.quantumleap.core.data.entity.Entity;
import cz.quantumleap.core.data.transport.Slice;
import cz.quantumleap.core.data.transport.SliceRequest;
import cz.quantumleap.core.data.transport.Table;
import cz.quantumleap.core.data.transport.Table.Column;
import cz.quantumleap.core.data.transport.TablePreferences;
import org.jooq.Record;
import org.jooq.RecordHandler;

import java.util.List;
import java.util.Map;

public class SliceMapper<TABLE extends org.jooq.Table<? extends Record>> implements RecordHandler<Record> {

    private final SliceRequest sliceRequest;
    private final List<TablePreferences> tablePreferencesList;
    private final TableMapper<TABLE> tableMapper;

    private int recordCount = 0;
    private boolean canExtend = false;

    SliceMapper(Entity<TABLE> entity, SliceRequest sliceRequest, List<TablePreferences> tablePreferencesList) {
        this.sliceRequest = sliceRequest;
        this.tablePreferencesList = tablePreferencesList;
        this.tableMapper = new TableMapper<>(entity, sliceRequest.getSort(), sliceRequest.getSize());
    }

    @Override
    public void next(Record record) {
        if (recordCount++ < sliceRequest.getSize()) {
            tableMapper.next(record);
        } else {
            canExtend = true;
        }
    }

    public Slice<Map<Column, Object>> intoSlice() {
        Table<Map<Column, Object>> table = tableMapper.intoTable(selectTablePreferences());
        return new Slice<>(table, sliceRequest, canExtend);
    }

    private TablePreferences selectTablePreferences() {
        for (TablePreferences preferences : tablePreferencesList) {
            if (preferences.isDefault()) {
                return preferences;
            }
        }
        return TablePreferences.EMPTY;
    }
}
