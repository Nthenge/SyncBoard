
CREATE TABLE faqs (
    id          BIGINT          NOT NULL AUTO_INCREMENT,
    question    VARCHAR(500)    NOT NULL,
    answer      TEXT            NOT NULL,
    active      BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at  DATETIME        NOT NULL,
    updated_at  DATETIME        NOT NULL,

    CONSTRAINT pk_faqs PRIMARY KEY (id),
    CONSTRAINT uq_faqs_question UNIQUE (question)
);