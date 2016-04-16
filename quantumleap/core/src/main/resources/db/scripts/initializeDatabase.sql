-- psql
CREATE USER quantumleap WITH PASSWORD 'quantumleap';
CREATE DATABASE quantumleap ENCODING = 'UTF8' LC_COLLATE = 'cs_CZ.UTF-8' TEMPLATE template0;
GRANT ALL PRIVILEGES ON DATABASE quantumleap TO quantumleap;

\c quantumleap
ALTER SCHEMA public OWNER TO quantumleap;
