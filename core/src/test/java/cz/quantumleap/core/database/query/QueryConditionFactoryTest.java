package cz.quantumleap.core.database.query;

import org.jooq.Condition;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static cz.quantumleap.core.database.query.QueryUtils.createFieldMap;
import static cz.quantumleap.core.tables.PersonTable.PERSON;

class QueryConditionFactoryTest {

    @Test
    public void validConditionIsCreatedForQuery() {
        QueryConditionFactory queryConditionFactory = createQueryConditionFactory();
        String query = "id > 1 (name = \"Forename Surname\" or email = Surname@company.cx) Title";

        Condition condition = queryConditionFactory.forQuery(query);

        Assertions.assertEquals("(\n" +
                "  cast(\"core\".\"person\".\"id\" as bigint) > 1\n" +
                "  and (\n" +
                "    cast(\"core\".\"person\".\"name\" as varchar) = 'Forename Surname'\n" +
                "    or cast(\"core\".\"person\".\"email\" as varchar) = 'Surname@company.cx'\n" +
                "  )\n" +
                "  and \"core\".\"person\".\"name\" like 'Title%'\n" +
                ")", condition.toString());
    }

    @Test
    public void validConditionIsCreatedForQueryWithMissingBrackets() {
        QueryConditionFactory queryConditionFactory = createQueryConditionFactory();
        String query = "id > 1 (name = \"Forename Surname\" or Title";

        Condition condition = queryConditionFactory.forQuery(query);

        Assertions.assertEquals("(\n" +
                "  cast(\"core\".\"person\".\"id\" as bigint) > 1\n" +
                "  and (\n" +
                "    cast(\"core\".\"person\".\"name\" as varchar) = 'Forename Surname'\n" +
                "    or \"core\".\"person\".\"name\" like 'Title%'\n" +
                "  )\n" +
                ")", condition.toString());
    }

    private QueryConditionFactory createQueryConditionFactory() {
        return new QueryConditionFactory(
                q -> PERSON.NAME.like(q + "%"),
                createFieldMap(PERSON.fields())
        );
    }
}