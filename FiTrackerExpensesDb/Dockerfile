FROM postgres:17.4-alpine3.21
ENV POSTGRES_PASSWORD=examplePassword
COPY ./init.sql /docker-entrypoint-initdb.d/init.sql
