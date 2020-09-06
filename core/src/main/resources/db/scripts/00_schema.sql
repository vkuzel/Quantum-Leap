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

CREATE TABLE core.table_preferences (
  id                BIGSERIAL PRIMARY KEY,
  entity_identifier VARCHAR(128) NOT NULL,
  is_default        BOOLEAN      NOT NULL,
  enabled_columns   VARCHAR[]    NOT NULL DEFAULT '{}',
  UNIQUE (entity_identifier, is_default)
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

CREATE OR REPLACE FUNCTION core.generate_intervals(intervals_start DATE, intervals_end DATE, step VARCHAR)
  RETURNS TABLE(interval_start TIMESTAMP, interval_end TIMESTAMP) AS $$
SELECT DATE_TRUNC(step, interval_start), DATE_TRUNC(step, interval_start) + ('1 ' || step) :: INTERVAL - '00:00:00.000001' :: INTERVAL
FROM generate_series(intervals_start :: TIMESTAMP, intervals_end, ('1 ' || step) :: INTERVAL) intervals (interval_start);
$$ IMMUTABLE LANGUAGE SQL;
