package cz.quantumleap.core.data.query;

import org.jooq.Condition;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static cz.quantumleap.core.tables.PersonTable.PERSON;

class FilterFactoryTest {

    @Test
    public void validConditionIsCreatedForQuery() {
        FilterFactory filterFactory = createFilterFactory();
        String query = "id > 1 (name = \"Forename Surname\" or email = Surname@company.cx) Title";

        Condition condition = filterFactory.forQuery(query);

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
    public void validConditionIsCreatedForQueryWithMissingParenteses() {
        FilterFactory filterFactory = createFilterFactory();
        String query = "id > 1 (name = \"Forename Surname\" or Title";

        Condition condition = filterFactory.forQuery(query);

        Assertions.assertEquals("(\n" +
                "  cast(\"core\".\"person\".\"id\" as bigint) > 1\n" +
                "  and (\n" +
                "    cast(\"core\".\"person\".\"name\" as varchar) = 'Forename Surname'\n" +
                "    or \"core\".\"person\".\"name\" like 'Title%'\n" +
                "  )\n" +
                ")", condition.toString());
    }

    private FilterFactory createFilterFactory() {
        return new FilterFactory(
                Arrays.asList(PERSON.fields()),
                null,
                q -> PERSON.NAME.like(q + "%")
        );
    }
}