CREATE FUNCTION core.first_agg(anyelement, anyelement)
    RETURNS anyelement
    LANGUAGE sql
    IMMUTABLE STRICT PARALLEL SAFE AS
'SELECT $1';

CREATE AGGREGATE core.first (anyelement) (
    SFUNC = core.first_agg,
    STYPE = anyelement,
    PARALLEL = safe
    );

CREATE FUNCTION core.last_agg(anyelement, anyelement)
    RETURNS anyelement
    LANGUAGE sql
    IMMUTABLE STRICT PARALLEL SAFE AS
'SELECT $2';

CREATE AGGREGATE core.last (anyelement) (
    SFUNC = core.last_agg,
    STYPE = anyelement,
    PARALLEL = safe
    );
