package cz.quantumleap.core.database.query;

import cz.quantumleap.core.database.domain.SliceRequest;
import cz.quantumleap.core.database.domain.TablePreferences;
import cz.quantumleap.core.database.domain.TableSlice;
import org.jooq.Result;

public interface TableSliceFactory {

    TableSlice forRequestedResult(TablePreferences tablePreferences, SliceRequest sliceRequest, Result<?> result);
}
