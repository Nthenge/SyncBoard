
CREATE TABLE talks (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    full_name   VARCHAR(100) NOT NULL,
    email       VARCHAR(150) NOT NULL,
    message     TEXT         NOT NULL,
    issue_id    BIGINT       NOT NULL,
    status      VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
    created_at  DATETIME     NOT NULL,
    updated_at  DATETIME     NOT NULL,

    CONSTRAINT pk_talks         PRIMARY KEY (id),
    CONSTRAINT fk_talks_issue   FOREIGN KEY (issue_id) REFERENCES issues(id)
);