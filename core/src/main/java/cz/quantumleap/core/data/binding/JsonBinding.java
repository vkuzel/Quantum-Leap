package cz.quantumleap.core.data.binding;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jooq.*;
import org.jooq.impl.DSL;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Types;
import java.util.Objects;

public class JsonBinding implements Binding<JSON, JsonNode> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public Converter<JSON, JsonNode> converter() {
        return new Converter<JSON, JsonNode>() {
            @Override
            public JsonNode from(JSON databaseObject) {
                try {
                    return databaseObject != null ? OBJECT_MAPPER.readValue("" + databaseObject, JsonNode.class) : null;
                } catch (IOException e) {
                    throw new IllegalStateException(e);
                }
            }

            @Override
            public JSON to(JsonNode userObject) {
                try {
                    if (userObject != null) {
                        String jsonString = OBJECT_MAPPER.writeValueAsString(userObject);
                        return stringToJson(jsonString);
                    } else {
                        return null;
                    }
                } catch (JsonProcessingException e) {
                    throw new IllegalStateException(e);
                }
            }

            @Override
            public Class<JSON> fromType() {
                return JSON.class;
            }

            @Override
            public Class<JsonNode> toType() {
                return JsonNode.class;
            }
        };
    }

    @Override
    public void sql(BindingSQLContext<JsonNode> ctx) throws SQLException {
        ctx.render().visit(DSL.val(ctx.convert(converter()).value())).sql("::json");
    }

    @Override
    public void register(BindingRegisterContext<JsonNode> ctx) throws SQLException {
        ctx.statement().registerOutParameter(ctx.index(), Types.OTHER);
    }

    @Override
    public void set(BindingSetStatementContext<JsonNode> ctx) throws SQLException {
        ctx.statement().setString(ctx.index(), Objects.toString(ctx.convert(converter()).value(), null));
    }

    @Override
    public void set(BindingSetSQLOutputContext<JsonNode> ctx) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public void get(BindingGetResultSetContext<JsonNode> ctx) throws SQLException {
        String jsonString = ctx.resultSet().getString(ctx.index());
        ctx.convert(converter()).value(stringToJson(jsonString));
    }

    @Override
    public void get(BindingGetStatementContext<JsonNode> ctx) throws SQLException {
        String jsonString = ctx.statement().getString(ctx.index());
        ctx.convert(converter()).value(stringToJson(jsonString));
    }

    @Override
    public void get(BindingGetSQLInputContext<JsonNode> ctx) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    private JSON stringToJson(String jsonString) {
        return jsonString != null ? JSON.valueOf(jsonString) : null;
    }
}
