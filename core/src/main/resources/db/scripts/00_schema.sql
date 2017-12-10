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
  id                              BIGSERIAL PRIMARY KEY,
  database_table_name_with_schema VARCHAR(128) NOT NULL,
  is_default                      BOOLEAN      NOT NULL,
  enabled_columns                 VARCHAR []   NOT NULL DEFAULT '{}',
  UNIQUE (database_table_name_with_schema, is_default)
);

CREATE TABLE core.enum (
  id   VARCHAR(255) NOT NULL PRIMARY KEY,
  name VARCHAR(255) NOT NULL
);

CREATE TABLE core.enum_value (
  id      VARCHAR(255) NOT NULL PRIMARY KEY,
  enum_id VARCHAR(255) NOT NULL REFERENCES core.enum,
  label   VARCHAR(255) NOT NULL,
  UNIQUE (id, enum_id)
);
