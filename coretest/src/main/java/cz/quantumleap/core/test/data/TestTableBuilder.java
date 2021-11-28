package cz.quantumleap.core.test.data;

import org.jooq.*;
import org.jooq.impl.DSL;
import org.jooq.impl.Internal;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TestTableBuilder {

    private Schema schema = DSL.schema("test_schema");
    private Name name = DSL.name("name");
    private final List<FieldDescriptor> fieldDescriptors = new ArrayList<>();
    private final List<ForeignKeyDescriptor> foreignKeyDescriptors = new ArrayList<>();

    public TestTableBuilder setSchema(Schema schema) {
        this.schema = schema;
        return this;
    }

    public TestTableBuilder setName(String name) {
        this.name = DSL.name(name);
        return this;
    }

    public TestTableBuilder addPrimaryKeyIdField() {
        Name fieldName = DSL.name("id");
        DataType<Long> fieldType = SQLDataType.BIGINT.nullable(false).identity(true);
        this.fieldDescriptors.add(new FieldDescriptor(fieldName, fieldType, true));
        return this;
    }

    public TestTableBuilder addField(String name, DataType<?> dataType) {
        this.fieldDescriptors.add(new FieldDescriptor(DSL.name(name), dataType, false));
        return this;
    }

    public TestTableBuilder addForeignKey(String fieldName, DataType<?> dataType, Table<?> referencedTable) {
        addField(fieldName, dataType);
        this.foreignKeyDescriptors.add(new ForeignKeyDescriptor(DSL.name(fieldName), referencedTable));
        return this;
    }

    public TestTable build() {
        return new TestTable(name, schema, fieldDescriptors, foreignKeyDescriptors);
    }

    public static class TestTable extends TableImpl<Record> {

        private UniqueKey<Record> primaryKey;
        private final List<ForeignKey<Record, ?>> references = new ArrayList<>();

        private TestTable(
                Name name,
                Schema schema,
                List<FieldDescriptor> fieldDescriptors,
                List<ForeignKeyDescriptor> foreignKeyDescriptors
        ) {
            super(name, schema);

            for (FieldDescriptor fieldDescriptor : fieldDescriptors) {
                TableField<Record, ?> field = createField(fieldDescriptor.name, fieldDescriptor.dataType);
                if (fieldDescriptor.primaryKey) {
                    if (primaryKey != null) {
                        String msg = String.format("Primary key for table %s already exists!", this);
                        throw new IllegalStateException(msg);
                    }
                    primaryKey = createPrimaryKey(field);
                }
            }

            for (ForeignKeyDescriptor descriptor : foreignKeyDescriptors) {
                Field<?> field = field(descriptor.fieldName);
                UniqueKey<?> referencedPrimaryKey = descriptor.referencedTable.getPrimaryKey();
                if (referencedPrimaryKey == null) {
                    String msgPattern = "Referenced table %s does not have primary key!";
                    throw new IllegalStateException(String.format(msgPattern, descriptor.referencedTable));
                }
                references.add(createForeignKey(field, referencedPrimaryKey));
            }
        }

        private UniqueKey<Record> createPrimaryKey(TableField<Record, ?> field) {
            return Internal.createUniqueKey(this, field);
        }

        @SuppressWarnings({"unchecked", "rawtypes"})
        private ForeignKey<Record, ?> createForeignKey(Field<?> field, UniqueKey<?> referencedPrimaryKey) {
            TableField[] fields = new TableField[]{(TableField) field};
            return Internal.createForeignKey(this, null, fields, referencedPrimaryKey, null, true);
        }

        @Override
        public UniqueKey<Record> getPrimaryKey() {
            return primaryKey;
        }

        @Override
        public List<UniqueKey<Record>> getKeys() {
            return Collections.singletonList(primaryKey);
        }

        @Override
        public List<ForeignKey<Record, ?>> getReferences() {
            return references;
        }
    }

    private static class FieldDescriptor {

        private final Name name;
        private final DataType<?> dataType;
        private final boolean primaryKey;

        private FieldDescriptor(Name name, DataType<?> dataType, boolean primaryKey) {
            this.name = name;
            this.dataType = dataType;
            this.primaryKey = primaryKey;
        }
    }

    private static class ForeignKeyDescriptor {

        private final Name fieldName;
        private final Table<?> referencedTable;

        public ForeignKeyDescriptor(Name fieldName, Table<?> referencedTable) {
            this.fieldName = fieldName;
            this.referencedTable = referencedTable;
        }
    }
}
