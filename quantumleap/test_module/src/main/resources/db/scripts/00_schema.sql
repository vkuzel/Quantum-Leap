CREATE SCHEMA test_module;

CREATE TABLE test_module.test_entity (
  id         BIGSERIAL PRIMARY KEY,
  name       JSON,
  comment    VARCHAR,
  created_at TIMESTAMP NOT NULL,
  created_by BIGINT    NOT NULL,
  updated_at TIMESTAMP,
  updated_by BIGINT
);
