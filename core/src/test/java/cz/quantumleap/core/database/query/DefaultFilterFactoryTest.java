package cz.quantumleap.core.database.query;

import org.jooq.Condition;
import org.jooq.Field;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static cz.quantumleap.core.tables.PersonTable.PERSON;

class DefaultFilterFactoryTest {

    @Test
    public void validConditionIsCreatedForQuery() {
        Map<String, Field<?>> fieldMap = Stream.of(PERSON.fields()).collect(Collectors.toMap(Field::getName, Function.identity()));
        FilterFactory filterFactory = createFilterFactory();
        String query = "id > 1 (name = \"Forename Surname\" or email = Surname@company.cx) Title";

        Condition condition = filterFactory.forQuery(fieldMap, query);

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
        Map<String, Field<?>> fieldMap = Stream.of(PERSON.fields()).collect(Collectors.toMap(Field::getName, Function.identity()));
        FilterFactory filterFactory = createFilterFactory();
        String query = "id > 1 (name = \"Forename Surname\" or Title";

        Condition condition = filterFactory.forQuery(fieldMap, query);

        Assertions.assertEquals("(\n" +
                "  cast(\"core\".\"person\".\"id\" as bigint) > 1\n" +
                "  and (\n" +
                "    cast(\"core\".\"person\".\"name\" as varchar) = 'Forename Surname'\n" +
                "    or \"core\".\"person\".\"name\" like 'Title%'\n" +
                "  )\n" +
                ")", condition.toString());
    }

    private FilterFactory createFilterFactory() {
        return new DefaultFilterFactory(
                null,
                q -> PERSON.NAME.like(q + "%")
        );
    }
}