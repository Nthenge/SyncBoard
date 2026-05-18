
CREATE TABLE system_configs (
    id           BIGINT       NOT NULL AUTO_INCREMENT,
    config_key   VARCHAR(50)  NOT NULL,
    config_value TEXT         NOT NULL,
    description  VARCHAR(300),
    created_at   DATETIME     NOT NULL,
    updated_at   DATETIME     NOT NULL,

    CONSTRAINT pk_system_configs     PRIMARY KEY (id),
    CONSTRAINT uq_system_config_key  UNIQUE (config_key)
);