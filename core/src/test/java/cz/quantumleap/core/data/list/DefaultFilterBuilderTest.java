package cz.quantumleap.core.data.list;

import cz.quantumleap.core.tables.PersonTable;
import org.jooq.Condition;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class DefaultFilterBuilderTest {

    @Test
    public void queryWithSpacesIsTokenized() {
        DefaultFilterBuilder defaultFilterBuilder = new DefaultFilterBuilder(null, null);
        String query = "  id  >  1  (  name  =  \"Forename Surname\"  or  email  =  Surname@company.cx  )  Title  ";

        List<String> tokens = defaultFilterBuilder.tokenize(query);

        Assert.assertArrayEquals(new String[]{"id", ">", "1", "(", "name", "=", "Forename Surname", "or", "email", "=", "Surname@company.cx", ")", "Title"}, tokens.toArray());
    }

    @Test
    public void queryWithoutSpacesIsTokenized() {
        DefaultFilterBuilder defaultFilterBuilder = new DefaultFilterBuilder(null, null);
        String query = "id>1(name=\"Forename Surname\"or email=Surname@company.cx)Title";

        List<String> tokens = defaultFilterBuilder.tokenize(query);

        Assert.assertArrayEquals(new String[]{"id", ">", "1", "(", "name", "=", "Forename Surname", "or", "email", "=", "Surname@company.cx", ")", "Title"}, tokens.toArray());
    }

    @Test
    public void queryWithMissingDoubleQuotesIsTokenized() {
        DefaultFilterBuilder defaultFilterBuilder = new DefaultFilterBuilder(null, null);
        String query = "first\"second  ";

        List<String> tokens = defaultFilterBuilder.tokenize(query);

        Assert.assertArrayEquals(new String[]{"first", "second  "}, tokens.toArray());
    }

    @Test
    public void validConditionIsCreated() {
        DefaultFilterBuilder defaultFilterBuilder = new DefaultFilterBuilder(PersonTable.PERSON, s -> PersonTable.PERSON.NAME.like(s + "%"));
        List<String> tokens = Arrays.asList("id", ">", "1", "(", "name", "=", "Forename Surname", "or", "email", "=", "Surname@company.cx", ")", "Title");

        Condition condition = defaultFilterBuilder.createCondition(tokens);

        Assert.assertEquals("(\n" +
                "  cast(\"core\".\"person\".\"id\" as bigint) > 1\n" +
                "  and (\n" +
                "    cast(\"core\".\"person\".\"name\" as varchar) = 'Forename Surname'\n" +
                "    or cast(\"core\".\"person\".\"email\" as varchar) = 'Surname@company.cx'\n" +
                "  )\n" +
                "  and \"core\".\"person\".\"name\" like 'Title%'\n" +
                ")", condition.toString());
    }

    @Test
    public void conditionWithMissingParenthesesIsHandled() {
        DefaultFilterBuilder defaultFilterBuilder = new DefaultFilterBuilder(PersonTable.PERSON, s -> PersonTable.PERSON.NAME.like(s + "%"));
        List<String> tokens = Arrays.asList("id", ">", "1", "(", "name", "=", "Forename Surname", "or", "Title");

        Condition condition = defaultFilterBuilder.createCondition(tokens);

        Assert.assertEquals("(\n" +
                "  cast(\"core\".\"person\".\"id\" as bigint) > 1\n" +
                "  and (\n" +
                "    cast(\"core\".\"person\".\"name\" as varchar) = 'Forename Surname'\n" +
                "    or \"core\".\"person\".\"name\" like 'Title%'\n" +
                "  )\n" +
                ")", condition.toString());
    }
}