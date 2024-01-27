-- psql
CREATE USER quantumleap WITH PASSWORD 'quantumleap';
CREATE DATABASE quantumleap ENCODING = 'UTF8' TEMPLATE template0;
GRANT ALL PRIVILEGES ON DATABASE quantumleap TO quantumleap;
ALTER SCHEMA public OWNER TO quantumleap;
