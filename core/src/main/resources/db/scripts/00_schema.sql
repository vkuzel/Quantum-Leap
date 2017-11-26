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
  person_id BIGINT NOT NULL REFERENCES core.person (id),
  role_id   BIGINT NOT NULL REFERENCES core.role (id),
  UNIQUE (person_id, role_id)
);

CREATE TABLE core.table_preferences (
  id                              BIGSERIAL PRIMARY KEY,
  database_table_name_with_schema VARCHAR(128) NOT NULL,
  is_default                      BOOLEAN      NOT NULL,
  enabled_columns                 VARCHAR []   NOT NULL DEFAULT '{}',
  UNIQUE (database_table_name_with_schema, is_default)
);
