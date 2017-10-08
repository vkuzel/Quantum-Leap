CREATE TABLE core.test_entity (
  id      BIGSERIAL,
  message VARCHAR(50)
);

CREATE TABLE core.related_test_entity (
  test_entity_id BIGINT,
  multi_lang     JSON
);