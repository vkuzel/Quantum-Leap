CREATE OR REPLACE FUNCTION core.generate_intervals(intervals_start DATE, intervals_end DATE, step VARCHAR)
  RETURNS TABLE(interval_start TIMESTAMP, interval_end TIMESTAMP) AS $$
SELECT DATE_TRUNC(step, interval_start), DATE_TRUNC(step, interval_start) + ('1 ' || step) :: INTERVAL - '00:00:00.000001' :: INTERVAL
FROM generate_series(intervals_start :: TIMESTAMP, intervals_end, ('1 ' || step) :: INTERVAL) intervals (interval_start);
$$ IMMUTABLE LANGUAGE SQL;
