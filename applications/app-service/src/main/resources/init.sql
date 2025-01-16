
CREATE TABLE IF NOT EXISTS external_service_history ( 
    id SERIAL PRIMARY KEY,
    date TIMESTAMP NOT NULL,
    endpoint VARCHAR(255) NOT NULL,
    percentage NUMERIC
);

CREATE TABLE IF NOT EXISTS main_service_history (
    id SERIAL PRIMARY KEY,
    date TIMESTAMP NOT NULL,
    endpoint VARCHAR(255) NOT NULL,
    first_number NUMERIC,
    second_number NUMERIC,
    result NUMERIC,
    external_service_id BIGINT, 
    FOREIGN KEY (external_service_id) REFERENCES external_service_history(id)
);
