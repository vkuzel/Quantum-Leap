CREATE SCHEMA core;

CREATE TABLE core.increment (
  id         BIGSERIAL PRIMARY KEY,
  module     VARCHAR(255) NOT NULL,
  version    INT          NOT NULL,
  file_name  VARCHAR(255) NOT NULL,
  created_at TIMESTAMP    NOT NULL
);

CREATE TABLE core.role (
  id   BIGSERIAL PRIMARY KEY,
  name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE core.person (
  id         BIGSERIAL PRIMARY KEY,
  email      VARCHAR(255) NOT NULL UNIQUE,
  name       VARCHAR(255),
  created_at TIMESTAMP    NOT NULL
);

CREATE TABLE core.person_role (
  id        BIGSERIAL PRIMARY KEY,
  person_id BIGINT NOT NULL REFERENCES core.person,
  role_id   BIGINT NOT NULL REFERENCES core.role,
  UNIQUE (person_id, role_id)
);

CREATE TABLE core.slice_query (
  id                BIGSERIAL PRIMARY KEY,
  entity_identifier VARCHAR(128) NOT NULL,
  person_id         BIGINT REFERENCES core.person,
  is_default        BOOLEAN      NOT NULL,
  name              VARCHAR      NOT NULL,
  query             VARCHAR      NOT NULL,
  UNIQUE (entity_identifier, person_id, is_default)
);

CREATE TABLE core.enum (
  id   VARCHAR(255) NOT NULL PRIMARY KEY,
  name VARCHAR(255) NOT NULL
);

CREATE TABLE core.enum_value (
  id      VARCHAR(255) NOT NULL,
  enum_id VARCHAR(255) NOT NULL REFERENCES core.enum,
  label   VARCHAR(255) NOT NULL,
  PRIMARY KEY (id, enum_id)
);

CREATE TABLE core.notification (
  id                BIGSERIAL PRIMARY KEY,
  code              VARCHAR   NOT NULL,
  message_arguments VARCHAR[] NOT NULL,
  person_id         BIGINT REFERENCES core.person,
  role_id           BIGINT REFERENCES core.role,
  created_at        TIMESTAMP NOT NULL,
  resolved_at       TIMESTAMP,
  resolved_by       BIGINT REFERENCES core.person
);

CREATE INDEX index_unresolved_notifications
  ON core.notification ((resolved_at IS NULL));

CREATE OR REPLACE FUNCTION core.generate_intervals(
    intervals_start DATE,
    intervals_end DATE,
    step VARCHAR,
    open_end BOOLEAN DEFAULT FALSE
) RETURNS TABLE(interval_start TIMESTAMP, interval_end TIMESTAMP) AS $$
WITH resolved AS (
    SELECT CASE UPPER(step) WHEN 'QUARTER' THEN '3 MONTH' ELSE ('1 ' || step) END :: INTERVAL AS interval
)
SELECT
    CASE
        WHEN open_end AND row_number() OVER (order by interval_start) = 1 THEN '4713-01-01 BC' :: TIMESTAMP
        ELSE DATE_TRUNC(step, interval_start)
        END,
    CASE
        WHEN open_end AND row_number() OVER (ORDER BY interval_start DESC) = 1 THEN '294276-01-01' :: TIMESTAMP
        ELSE DATE_TRUNC(step, interval_start) + r.interval - '00:00:00.000001' :: INTERVAL
        END
FROM resolved r
         CROSS JOIN generate_series(intervals_start :: TIMESTAMP, intervals_end, r.interval) intervals (interval_start);
$$ IMMUTABLE LANGUAGE SQL;

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

CREATE EXTENSION IF NOT EXISTS unaccent;
