DROP FUNCTION IF EXISTS core.generate_intervals(
    intervals_start DATE,
    intervals_end DATE,
    step VARCHAR
);

CREATE OR REPLACE FUNCTION core.generate_intervals(
    intervals_start DATE,
    intervals_end DATE,
    step VARCHAR,
    open_end BOOLEAN DEFAULT FALSE
) RETURNS TABLE(interval_start TIMESTAMP, interval_end TIMESTAMP) AS $$
WITH resolved AS (
    SELECT CASE UPPER(step) WHEN 'QUARTER' THEN '3 MONTH' ELSE ('1 ' || step) END :: INTERVAL AS interval
)
SELECT
    CASE
        WHEN open_end AND row_number() OVER (order by interval_start) = 1 THEN '4713-01-01 BC' :: TIMESTAMP
        ELSE DATE_TRUNC(step, interval_start)
        END,
    CASE
        WHEN open_end AND row_number() OVER (ORDER BY interval_start DESC) = 1 THEN '294276-01-01' :: TIMESTAMP
        ELSE DATE_TRUNC(step, interval_start) + r.interval - '00:00:00.000001' :: INTERVAL
        END
FROM resolved r
         CROSS JOIN generate_series(intervals_start :: TIMESTAMP, intervals_end, r.interval) intervals (interval_start);
$$ IMMUTABLE LANGUAGE SQL;
