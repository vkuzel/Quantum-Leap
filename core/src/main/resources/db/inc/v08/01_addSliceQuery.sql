CREATE TABLE core.slice_query (
  id                BIGSERIAL PRIMARY KEY,
  entity_identifier VARCHAR(128) NOT NULL,
  person_id         BIGINT REFERENCES core.person,
  is_default        BOOLEAN      NOT NULL,
  name              VARCHAR      NOT NULL,
  query             VARCHAR      NOT NULL,
  UNIQUE (entity_identifier, person_id, is_default)
);
