CREATE TABLE test_entity (
  id      BIGSERIAL PRIMARY KEY,
  comment VARCHAR
);

CREATE OR REPLACE FUNCTION process_text(input VARCHAR)
  RETURNS VARCHAR AS $$
BEGIN
  RETURN 'processed: ' || input;
END;
$$ LANGUAGE plpgsql;
