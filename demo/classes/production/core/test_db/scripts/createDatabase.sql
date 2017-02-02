-- psql
CREATE USER quantumleap_test WITH PASSWORD 'quantumleap_test';
CREATE DATABASE quantumleap_test ENCODING = 'UTF8' LC_COLLATE = 'cs_CZ.UTF-8' TEMPLATE template0;
GRANT ALL PRIVILEGES ON DATABASE quantumleap_test TO quantumleap_test;
