package cz.quantumleap.core.enums;

import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import static cz.quantumleap.core.tables.EnumValueTable.ENUM_VALUE;

@Repository
public class EnumDao {

    private final DSLContext dslContext;

    public EnumDao(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    public String fetchDefaultEnumValue(String enumId) {
        return dslContext.select(ENUM_VALUE.ID)
                .from(ENUM_VALUE)
                .where(ENUM_VALUE.ENUM_ID.eq(enumId))
                // natural order / random value
                .limit(1)
                .fetchOne(ENUM_VALUE.ID);
    }

}
