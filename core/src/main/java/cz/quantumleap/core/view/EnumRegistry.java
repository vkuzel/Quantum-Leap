package cz.quantumleap.core.view;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.collect.Tables;
import cz.quantumleap.core.database.domain.IdLabel;
import org.jooq.DSLContext;
import org.jooq.Record3;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static cz.quantumleap.core.tables.EnumValueTable.ENUM_VALUE;

@Component
public class EnumRegistry {

    private final DSLContext dslContext;

    private volatile Table<String, String, String> enumValueLabels;

    public EnumRegistry(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    @SuppressWarnings("unused")
    public List<IdLabel> getValueLabels(String enumId) {
        return enumValueLabels.row(enumId).entrySet()
                .stream()
                .map(e -> new IdLabel(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }

    @SuppressWarnings("unused")
    public String getLabel(String enumId, String value) {
        return enumValueLabels.get(enumId, value);
    }

    @EventListener(ContextRefreshedEvent.class)
    public void loadEnums() {
        enumValueLabels = dslContext.select(ENUM_VALUE.ENUM_ID, ENUM_VALUE.ID, ENUM_VALUE.LABEL)
                .from(ENUM_VALUE)
                .fetchStream()
                .collect(Tables.toTable(Record3::value1, Record3::value2, Record3::value3, HashBasedTable::create));
    }
}
