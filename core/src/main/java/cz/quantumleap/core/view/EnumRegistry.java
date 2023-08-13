package cz.quantumleap.core.view;

import cz.quantumleap.core.database.domain.IdLabel;
import org.jooq.DSLContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

import static cz.quantumleap.core.tables.EnumValueTable.ENUM_VALUE;
import static java.lang.String.CASE_INSENSITIVE_ORDER;

@Component
public class EnumRegistry {

    private final DSLContext dslContext;

    private volatile Map<String, Map<String, String>> enumValueLabels;

    public EnumRegistry(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    @SuppressWarnings("unused")
    public List<IdLabel> getValueLabels(String enumId) {
        return enumValueLabels.getOrDefault(enumId, Map.of())
                .entrySet().stream()
                .map(e -> new IdLabel(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }

    @SuppressWarnings("unused")
    public String getLabel(String enumId, String value) {
        return enumValueLabels.getOrDefault(enumId, Map.of()).get(value);
    }

    @EventListener(ContextRefreshedEvent.class)
    public void loadEnums() {
        enumValueLabels = new HashMap<>();
        dslContext
                .select(ENUM_VALUE.ENUM_ID, ENUM_VALUE.ID, ENUM_VALUE.LABEL)
                .from(ENUM_VALUE)
                .fetchStream()
                .map(record -> new EnumIdLabel(record.value1(), record.value2(), record.value3()))
                .sorted(Comparator.comparing(EnumIdLabel::label, CASE_INSENSITIVE_ORDER))
                .forEach(enumIdLabel -> {
                    var idLabel = enumValueLabels
                            .computeIfAbsent(enumIdLabel.enumId, s -> new LinkedHashMap<>());
                    idLabel.put(enumIdLabel.id(), enumIdLabel.label());
                });
    }

    private record EnumIdLabel(
            String enumId,
            String id,
            String label
    ) {
    }
}
