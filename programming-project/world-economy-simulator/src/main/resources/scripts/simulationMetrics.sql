USE simulation;

DROP TABLE IF EXISTS country_metrics;
CREATE TABLE country_metrics
(
    id                BIGINT PRIMARY KEY AUTO_INCREMENT,
    day               INT    NOT NULL,
    country_id        BIGINT NOT NULL,
    population        BIGINT NOT NULL,
    money             DOUBLE NOT NULL,
    average_happiness DOUBLE NOT NULL,
    individual_budget DOUBLE NOT NULL,
    FOREIGN KEY (country_id) REFERENCES country (id)
);

DROP TABLE IF EXISTS resource_metrics;
CREATE TABLE resource_metrics
(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    day         INT    NOT NULL,
    country_id  BIGINT NOT NULL,
    resource_id BIGINT NOT NULL,
    quantity    INT    NOT NULL,
    value       DOUBLE NOT NULL,
    FOREIGN KEY (country_id) REFERENCES country (id),
    FOREIGN KEY (resource_id) REFERENCES resource (id)
);

DROP TABLE IF EXISTS resource_node_metrics;
CREATE TABLE resource_node_metrics
(
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    day             INT    NOT NULL,
    country_id      BIGINT NOT NULL,
    resource_id     BIGINT NOT NULL,
    production_cost DOUBLE NOT NULL,
    max_capacity    INT    NOT NULL,
    tier            INT    NOT NULL,
    FOREIGN KEY (country_id) REFERENCES country (id),
    FOREIGN KEY (resource_id) REFERENCES resource (id)
);