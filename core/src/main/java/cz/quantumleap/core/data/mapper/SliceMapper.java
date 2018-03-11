package cz.quantumleap.core.data.mapper;

import cz.quantumleap.core.data.EnumManager;
import cz.quantumleap.core.data.LookupDaoManager;
import cz.quantumleap.core.data.primarykey.PrimaryKeyResolver;
import cz.quantumleap.core.data.transport.Slice;
import cz.quantumleap.core.data.transport.SliceRequest;
import cz.quantumleap.core.data.transport.Table;
import cz.quantumleap.core.data.transport.TablePreferences;
import org.jooq.Record;
import org.jooq.RecordHandler;

import java.util.List;
import java.util.Map;

public class SliceMapper implements RecordHandler<Record> {

    private final SliceRequest sliceRequest;
    private final List<TablePreferences> tablePreferencesList;
    private final TableMapper tableMapper;

    private int recordCount = 0;
    private boolean canExtend = false;

    SliceMapper(org.jooq.Table<? extends Record> table, PrimaryKeyResolver primaryKeyResolver, LookupDaoManager lookupDaoManager, EnumManager enumManager, SliceRequest sliceRequest, List<TablePreferences> tablePreferencesList) {
        this.sliceRequest = sliceRequest;
        this.tablePreferencesList = tablePreferencesList;
        this.tableMapper = new TableMapper(table, primaryKeyResolver, lookupDaoManager, enumManager, sliceRequest.getSort(), sliceRequest.getSize());
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
        Table<Map<Table.Column, Object>> table = tableMapper.intoTable(selectTablePreferences());
        return new Slice<>(table, sliceRequest, canExtend);
    }

    private TablePreferences selectTablePreferences() {
        TablePreferences tablePreferences = TablePreferences.EMPTY;
        for (TablePreferences preferences : tablePreferencesList) {
            if (preferences == TablePreferences.EMPTY) {
                tablePreferences = preferences;
            } else if (preferences.isDefault()) {
                tablePreferences = preferences;
                break;
            }
        }
        return tablePreferences;
    }
}
