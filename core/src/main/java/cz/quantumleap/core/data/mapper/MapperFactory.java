package cz.quantumleap.core.data.mapper;

import cz.quantumleap.core.data.EnumManager;
import cz.quantumleap.core.data.LookupDao;
import cz.quantumleap.core.data.LookupDaoManager;
import cz.quantumleap.core.data.entity.Entity;
import cz.quantumleap.core.data.entity.EntityIdentifier;
import cz.quantumleap.core.data.transport.*;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Pair;
import org.jooq.Table;
import org.jooq.*;
import org.jooq.types.YearToSecond;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.CaseFormat.LOWER_UNDERSCORE;
import static com.google.common.base.CaseFormat.UPPER_CAMEL;

public class MapperFactory<TABLE extends Table<? extends Record>> {

    private final Entity<TABLE> entity;

    public MapperFactory(Entity<TABLE> entity) {
        this.entity = entity;
    }

    public SliceMapper<TABLE> createSliceMapper(SliceRequest sliceRequest, List<TablePreferences> tablePreferencesList) {
        return new SliceMapper<>(entity, sliceRequest, tablePreferencesList);
    }
}
