package cz.quantumleap.core.database.query;

import org.jooq.Condition;
import org.jooq.Field;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static cz.quantumleap.core.tables.PersonTable.PERSON;

class FilterFactoryTest {

    @Test
    public void validConditionIsCreatedForQuery() {
        List<Field<?>> fields = Arrays.asList(PERSON.fields());
        FilterFactory filterFactory = createFilterFactory();
        String query = "id > 1 (name = \"Forename Surname\" or email = Surname@company.cx) Title";

        Condition condition = filterFactory.forQuery(fields, query);

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
        List<Field<?>> fields = Arrays.asList(PERSON.fields());
        FilterFactory filterFactory = createFilterFactory();
        String query = "id > 1 (name = \"Forename Surname\" or Title";

        Condition condition = filterFactory.forQuery(fields, query);

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
                null,
                q -> PERSON.NAME.like(q + "%")
        );
    }
}