package cz.quantumleap.core.slicequery;

import cz.quantumleap.core.database.entity.EntityIdentifier;
import cz.quantumleap.core.security.Authenticator;
import cz.quantumleap.core.slicequery.domain.SliceQuery;
import org.jooq.DSLContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Repository;

import java.util.List;

import static cz.quantumleap.core.tables.PersonTable.PERSON;
import static cz.quantumleap.core.tables.SliceQueryTable.SLICE_QUERY;

@Repository
public class SliceQueryDao {

    private final DSLContext dslContext;
    private final Authenticator authenticator = new Authenticator();

    public SliceQueryDao(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    public List<SliceQuery> fetchByIdentifierForCurrentUser(EntityIdentifier<?> entityIdentifier) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var email = authenticator.getAuthenticationEmail(authentication);

        return dslContext.select(SLICE_QUERY.fields())
                .from(SLICE_QUERY)
                .leftJoin(PERSON).on(SLICE_QUERY.PERSON_ID.eq(PERSON.ID))
                .where(SLICE_QUERY.ENTITY_IDENTIFIER.eq(entityIdentifier.toString())
                        .and(SLICE_QUERY.PERSON_ID.isNull().or(PERSON.EMAIL.eq(email))))
                .orderBy(SLICE_QUERY.NAME.asc())
                .fetchInto(SliceQuery.class);
    }
}
