package cz.quantumleap.core.enums;

import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

@Repository
public class EnumDao {

    private final DSLContext dslContext;

    public EnumDao(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    public String fetchDefaultEnumValue(String enumId) {
        return dslContext.fetchOptional("""
                        SELECT id
                        FROM core.enum_value
                        WHERE enum_id = {0}
                        -- natural order / random value
                        LIMIT 1
                        """, enumId)
                .map(r -> r.into(String.class))
                .orElse(null);
    }

}
