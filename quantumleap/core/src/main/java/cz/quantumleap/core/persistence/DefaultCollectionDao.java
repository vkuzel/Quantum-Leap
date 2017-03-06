package cz.quantumleap.core.persistence;

import cz.quantumleap.core.persistence.collection.LimitBuilder;
import cz.quantumleap.core.persistence.collection.OrderBuilder;
import cz.quantumleap.core.persistence.transport.Slice;
import cz.quantumleap.core.persistence.transport.SliceRequest;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.TableField;
import org.springframework.data.domain.Sort;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

// TODO Table dao...?s
public class DefaultCollectionDao<TABLE extends Table<? extends Record>> implements CollectionDao<TABLE> {

    protected final Table<? extends Record> table;
    protected final DSLContext dslContext;

    protected final OrderBuilder orderBuilder;
    protected final LimitBuilder limitBuilder;
    protected final MapperFactory mapperFactory;

    public DefaultCollectionDao(Table<? extends Record> table, DSLContext dslContext, OrderBuilder orderBuilder, LimitBuilder limitBuilder, MapperFactory mapperFactory) {
        this.table = table;
        this.dslContext = dslContext;

        this.orderBuilder = orderBuilder;
        this.limitBuilder = limitBuilder;
        this.mapperFactory = mapperFactory;
    }

    public Slice fetchSlice(SliceRequest sliceRequest) {

        SliceRequest request = setDefaultOrder(sliceRequest);

        return dslContext.selectFrom(table)
                .orderBy(orderBuilder.build(request.getSort()))
                .limit(request.getOffset(), Math.min(request.getSize() + 1, SliceRequest.MAX_ITEMS))
                .fetchInto(mapperFactory.createSliceMapper(sliceRequest))
                .intoSlice();
    }

    private SliceRequest setDefaultOrder(SliceRequest sliceRequest) {
        if (sliceRequest.getSort() == null) {
            List<? extends TableField<? extends Record, ?>> primaryKeyFields = getPrimaryKeyFields();
            List<Sort.Order> orders = primaryKeyFields.stream()
                    .map(field -> new Sort.Order(Sort.Direction.DESC, field.getName()))
                    .collect(Collectors.toList());
            return new SliceRequest(
                    sliceRequest.getOffset(),
                    sliceRequest.getSize(),
                    !orders.isEmpty() ? new Sort(orders) : null // TODO Solve for tables without primary key...
            );
        }
        return sliceRequest;
    }

    private List<? extends TableField<? extends Record, ?>> getPrimaryKeyFields() {
        if (table.getPrimaryKey() != null) {
            return table.getPrimaryKey().getFields();
        }
        return Collections.emptyList();
    }
}
