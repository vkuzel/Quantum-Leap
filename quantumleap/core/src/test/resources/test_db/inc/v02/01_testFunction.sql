CREATE OR REPLACE FUNCTION increment(i INTEGER)
  RETURNS INTEGER AS $$
BEGIN
  RETURN i + 1;
END;
$$ LANGUAGE plpgsql;
