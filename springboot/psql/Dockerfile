FROM postgres:9.6-alpine
COPY Database_DDL.sql /docker-entrypoint-initdb.d/1-database.sql
COPY Table_DDL.sql /docker-entrypoint-initdb.d/2-table_ddl.sql