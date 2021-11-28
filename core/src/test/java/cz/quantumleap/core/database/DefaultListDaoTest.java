package cz.quantumleap.core.database;

import cz.quantumleap.core.database.domain.FetchParams;
import cz.quantumleap.core.database.domain.Slice;
import cz.quantumleap.core.database.domain.Slice.Column;
import cz.quantumleap.core.database.entity.Entity;
import cz.quantumleap.core.database.entity.EntityIdentifier;
import cz.quantumleap.core.slicequery.SliceQueryDao;
import cz.quantumleap.core.test.CoreSpringBootTest;
import cz.quantumleap.core.test.common.CoreTestSupport;
import cz.quantumleap.core.test.data.TestTableBuilder;
import cz.quantumleap.core.test.data.TestTableBuilder.TestTable;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Table;
import org.jooq.impl.SQLDataType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.data.domain.Sort.Direction.ASC;

@CoreSpringBootTest
class DefaultListDaoTest {

    @Autowired
    private CoreTestSupport coreTestSupport;
    @Autowired
    private DSLContext dslContext;
    @Autowired
    private EntityRegistry entityRegistry;
    @Autowired
    private SliceQueryDao sliceQueryDao;

    @Test
    void fetchSliceGeneratesCorrectColumnTypes() {
        Entity<TestTable> entity = createEntity();
        Entity<TestTable> referencingEntity = createReferencingEntity(entity.getTable());
        DefaultListDao<TestTable> defaultListDao = createDefaultListDao(referencingEntity);

        FetchParams fetchParams = FetchParams.empty();
        Slice slice = defaultListDao.fetchSlice(fetchParams);

        assertEquals(2, slice.getColumns().size());
        assertTrue(slice.getColumnByName("id").isPrimaryKey());
        assertTrue(slice.getColumnByName("entity").isLookup());
    }

    @Test
    void fetchSliceCorrectlySortsLookupColumn() {
        Entity<TestTable> entity = createEntity();
        Entity<TestTable> referencingEntity = createReferencingEntity(entity.getTable());

        coreTestSupport.insertIntoTable(entity.getTable(), 1, "Aaa");
        coreTestSupport.insertIntoTable(entity.getTable(), 2, "Bbb");
        coreTestSupport.insertIntoTable(entity.getTable(), 3, "Ccc");

        coreTestSupport.insertIntoTable(referencingEntity.getTable(), 1, 3);
        coreTestSupport.insertIntoTable(referencingEntity.getTable(), 2, 2);
        coreTestSupport.insertIntoTable(referencingEntity.getTable(), 3, 1);

        DefaultListDao<TestTable> defaultListDao = createDefaultListDao(referencingEntity);

        FetchParams fetchParams = FetchParams.empty().withSort(Sort.by(ASC, "entity"));
        Slice slice = defaultListDao.fetchSlice(fetchParams);

        assertEquals(3, slice.getRows().size());
        assertEquals(3L, getValue(slice, "id", 0));
        assertEquals(2L, getValue(slice, "id", 1));
        assertEquals(1L, getValue(slice, "id", 2));
    }

    @Test
    void fetchSliceFiltersLookupColumnByLabelValue() {
        Entity<TestTable> entity = createEntity();
        Entity<TestTable> referencingEntity = createReferencingEntity(entity.getTable());

        coreTestSupport.insertIntoTable(entity.getTable(), 1, "Aaa");
        coreTestSupport.insertIntoTable(entity.getTable(), 2, "Bbb");

        coreTestSupport.insertIntoTable(referencingEntity.getTable(), 1, 1);
        coreTestSupport.insertIntoTable(referencingEntity.getTable(), 2, 2);

        DefaultListDao<TestTable> defaultListDao = createDefaultListDao(referencingEntity);

        FetchParams fetchParams = FetchParams.empty().addFilter("entity", "Aaa");
        Slice slice = defaultListDao.fetchSlice(fetchParams);

        assertEquals(1, slice.getRows().size());
        assertEquals(1L, getValue(slice, "id", 0));
    }

    private Entity<TestTable> createEntity() {
        TestTable testTable = new TestTableBuilder()
                .setName("entity")
                .addPrimaryKeyIdField()
                .addField("name", SQLDataType.VARCHAR)
                .build();

        coreTestSupport.dropTable(testTable);
        coreTestSupport.createTable(testTable);

        Entity<TestTable> entity = Entity.builder(testTable)
                .setLookupLabelFieldBuilder(table -> table.field("name", String.class))
                .build();

        entityRegistry.addLookupEntity(entity);

        return entity;
    }

    private Entity<TestTable> createReferencingEntity(Table<?> referencedTable) {
        TestTable testTable = new TestTableBuilder()
                .setName("referencing_entity")
                .addPrimaryKeyIdField()
                .addForeignKey("entity_id", SQLDataType.BIGINT, referencedTable)
                .build();

        coreTestSupport.dropTable(testTable);
        coreTestSupport.createTable(testTable);

        Field<?> lookupField = testTable.field("entity_id");
        return Entity.builder(testTable)
                .addLookupMetaType(lookupField, EntityIdentifier.forTable(referencedTable))
                .build();
    }

    private DefaultListDao<TestTable> createDefaultListDao(Entity<TestTable> entity) {
        return new DefaultListDao<>(entity, dslContext, entityRegistry, sliceQueryDao);
    }

    private Object getValue(Slice slice, String columnName, int rowIndex) {
        Column column = slice.getColumnByName(columnName);
        List<Object> row = slice.getRows().get(rowIndex);
        return slice.getValue(column, row);
    }
}