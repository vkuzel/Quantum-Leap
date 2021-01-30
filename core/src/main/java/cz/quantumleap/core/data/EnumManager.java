package cz.quantumleap.core.data;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.collect.Tables;
import cz.quantumleap.core.data.transport.IdLabel;
import cz.quantumleap.core.data.transport.SetValues;
import cz.quantumleap.core.data.transport.SetValues.Value;
import cz.quantumleap.core.data.transport.SetValues.ValueSet;
import org.jooq.DSLContext;
import org.jooq.Record3;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cz.quantumleap.core.tables.EnumValueTable.ENUM_VALUE;

@Component
public class EnumManager {

    private final DSLContext dslContext;

    private volatile Table<String, String, String> enumValueLabels;

    public EnumManager(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    public boolean isEnum(String enumId) {
        return enumValueLabels.containsRow(enumId);
    }

    public List<IdLabel> getValueLabels(String enumId) {
        return enumValueLabels.row(enumId).entrySet()
                .stream()
                .map(e -> new IdLabel(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }

    public SetValues createSet(String enumId, Collection<String> valueIds) {
        ValueSet values;
        if (valueIds.isEmpty()) {
            values = new ValueSet();
        } else {
            Map<String, String> enumLabels = enumValueLabels.row(enumId);
            values = new ValueSet(valueIds.size());
            for (String valueId : valueIds) {
                String label = enumLabels.getOrDefault(valueId, valueId);
                values.add(new Value(valueId, label));
            }
        }
        return new SetValues(enumId, values);
    }

    @EventListener(ContextRefreshedEvent.class)
    public void loadEnums() {
        enumValueLabels = dslContext.select(ENUM_VALUE.ENUM_ID, ENUM_VALUE.ID, ENUM_VALUE.LABEL)
                .from(ENUM_VALUE)
                .fetchStream()
                .collect(Tables.toTable(Record3::value1, Record3::value2, Record3::value3, HashBasedTable::create));
    }
}
