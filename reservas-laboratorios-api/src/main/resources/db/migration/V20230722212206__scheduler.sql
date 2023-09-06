CREATE TABLE task
(
    id          SERIAL PRIMARY KEY,
    semester_id INT         NOT NULL references semester (id),
    status      VARCHAR(10) NOT NULL,
    error_msg   TEXT,
    created     TIMESTAMP   NOT NULL DEFAULT NOW(),
    updated     TIMESTAMP   NOT NULL DEFAULT NOW()
);