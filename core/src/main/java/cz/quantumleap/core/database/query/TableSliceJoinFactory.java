package cz.quantumleap.core.database.query;

import cz.quantumleap.core.database.domain.SliceRequest;
import org.jooq.Record;
import org.jooq.SelectJoinStep;

import java.util.function.Function;

public interface TableSliceJoinFactory {

    Function<SelectJoinStep<Record>, SelectJoinStep<Record>> forSliceRequest(SliceRequest sliceRequest);
}
