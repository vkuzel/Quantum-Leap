CREATE OR REPLACE FUNCTION core.generate_intervals(intervals_start DATE, intervals_end DATE, step VARCHAR)
    RETURNS TABLE(interval_start TIMESTAMP, interval_end TIMESTAMP) AS $$
WITH resolved AS (
    SELECT CASE UPPER(step) WHEN 'QUARTER' THEN '3 MONTH' ELSE ('1 ' || step) END :: INTERVAL AS interval
)
SELECT
    DATE_TRUNC(step, interval_start),
    DATE_TRUNC(step, interval_start) + r.interval - '00:00:00.000001' :: INTERVAL
FROM resolved r
CROSS JOIN generate_series(intervals_start :: TIMESTAMP, intervals_end, r.interval) intervals (interval_start);
$$ IMMUTABLE LANGUAGE SQL;
