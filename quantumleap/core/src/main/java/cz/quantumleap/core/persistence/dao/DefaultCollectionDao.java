package cz.quantumleap.core.persistence.dao;

import cz.quantumleap.core.persistence.dao.collection.LimitBuilder;
import cz.quantumleap.core.persistence.dao.collection.OrderBuilder;
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
public class DefaultCollectionDao implements CollectionDao {

    protected final Table<Record> table;
    protected final DSLContext dslContext;

    protected final OrderBuilder orderBuilder;
    protected final LimitBuilder limitBuilder;
    protected final MapperFactory mapperFactory;

    public DefaultCollectionDao(Table<Record> table, DSLContext dslContext, OrderBuilder orderBuilder, LimitBuilder limitBuilder, MapperFactory mapperFactory) {
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
            List<TableField<Record, ?>> primaryKeyFields = getPrimaryKeyFields();
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

    private List<TableField<Record, ?>> getPrimaryKeyFields() {
        if (table.getPrimaryKey() != null) {
            return table.getPrimaryKey().getFields();
        }
        return Collections.emptyList();
    }
}
