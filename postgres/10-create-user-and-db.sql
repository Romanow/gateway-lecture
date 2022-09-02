-- file: 10-create-user-and-db.sql
CREATE DATABASE dictionary;
CREATE ROLE program WITH PASSWORD 'test';
GRANT ALL PRIVILEGES ON DATABASE dictionary TO program;
ALTER ROLE program WITH LOGIN;