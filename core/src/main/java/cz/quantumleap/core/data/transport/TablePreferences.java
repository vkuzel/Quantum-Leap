package cz.quantumleap.core.data.transport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TablePreferences {

    public static final TablePreferences EMPTY = new TablePreferences(-1, false, new String[]{});

    private final long id;
    private final boolean isDefault;
    // TODO Change this to List<String> with upgrade to jOOQ 3.10
    private final String[] enabledColumns;

    public TablePreferences(long id, boolean isDefault, String[] enabledColumns) {
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

    public String[] getEnabledColumns() {
        return enabledColumns;
    }

    public List<String> getEnabledColumnsAsList() {
        return Arrays.asList(enabledColumns);
    }
}
