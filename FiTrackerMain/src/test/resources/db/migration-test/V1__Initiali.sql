CREATE USER exampleuser WITH PASSWORD 'examplePassword';

CREATE SCHEMA app_core;
ALTER SCHEMA app_core OWNER TO exampleuser;
GRANT USAGE ON SCHEMA app_core TO exampleuser;





CREATE TABLE fit.app_core.app_user (
    id BIGSERIAL PRIMARY KEY,
    login VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    salt VARCHAR(255) NOT NULL,
    unique_id UUID NOT NULL UNIQUE,
    created_at TIMESTAMP  NOT NULL,
    updated_at TIMESTAMP  NOT NULL
);

GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA app_core TO exampleuser;
GRANT USAGE, SELECT, UPDATE ON ALL SEQUENCES IN SCHEMA app_core TO exampleuser;