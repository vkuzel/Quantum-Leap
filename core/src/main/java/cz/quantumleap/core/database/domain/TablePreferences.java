package cz.quantumleap.core.database.domain;

import java.util.Collections;
import java.util.List;

public class TablePreferences {

    public static final TablePreferences EMPTY = new TablePreferences(-1, false, Collections.emptyList());

    private final long id;
    private final boolean isDefault;
    private final List<String> enabledColumns;

    public TablePreferences(long id, boolean isDefault, List<String> enabledColumns) {
        this.id = id;
        this.isDefault = isDefault;
        this.enabledColumns = enabledColumns;
    }

    public long getId() {
        return id;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public List<String> getEnabledColumns() {
        return enabledColumns;
    }
}
