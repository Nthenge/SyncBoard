
CREATE TABLE issues (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    name        VARCHAR(100) NOT NULL,
    description VARCHAR(300),
    active      BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at  DATETIME     NOT NULL,
    updated_at  DATETIME     NOT NULL,

    CONSTRAINT pk_issues      PRIMARY KEY (id),
    CONSTRAINT uq_issue_name  UNIQUE (name)
);