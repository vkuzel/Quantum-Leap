CREATE TABLE test_entity (
  id      BIGSERIAL,
  message VARCHAR(50)
);

CREATE TABLE related_test_entity (
  test_entity_id BIGINT,
  multi_lang     JSON
);