
CREATE SCHEMA app_data;
ALTER SCHEMA app_data OWNER TO exampleuser;
GRANT USAGE ON SCHEMA app_data TO exampleuser;





CREATE TABLE fit.app_data.app_user (
    id BIGSERIAL PRIMARY KEY,
    app_user_id  BIGINT NOT NULL,
    app_zone_id VARCHAR(255) NOT NULL
);

CREATE TABLE fit.app_data.categories (
    category_id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    zone_id VARCHAR(255) NOT NULL,
    created_at TIMESTAMP  NOT NULL,
    updated_at TIMESTAMP  NOT NULL
);

CREATE TABLE fit.app_data.expenses (
    expense_id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    description VARCHAR(255),
    value DECIMAL NOT NULL,
    created_at TIMESTAMP  NOT NULL,
    updated_at TIMESTAMP  NOT NULL
);


CREATE TABLE fit.app_data.exp_categories(
    category_id BIGINT NOT NULL,
    expense_id BIGINT NOT NULL,
    PRIMARY KEY (category_id, expense_id),
    FOREIGN KEY (category_id) REFERENCES app_data.categories(category_id) ON DELETE CASCADE,
    FOREIGN KEY (expense_id) REFERENCES app_data.expenses(expense_id) ON DELETE CASCADE
);

GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA app_data TO exampleuser;
GRANT USAGE, SELECT, UPDATE ON ALL SEQUENCES IN SCHEMA app_data TO exampleuser;