create table reservation
(
    id                    SERIAL primary key,
    public_id             INT          NOT NULL,
    type                  VARCHAR(255) NOT NULL,
    schedule              VARCHAR(255) NOT NULL,
    degree_id             INT          NOT NULL,
    subject_id            INT          NOT NULL,
    semester_id           INT          NOT NULL,
    professor_id          INT          NOT NULL,
    responsible_id        INT          NOT NULL,
    department_id         INT,
    start_date            DATE         NOT NULL,
    end_date              DATE         NOT NULL,
    day                   VARCHAR(255) NOT NULL,
    start_time            TIME         NOT NULL,
    end_time              TIME         NOT NULL,
    teaching_type         VARCHAR(255),
    laboratory_preference VARCHAR(255),
    location              VARCHAR(255),
    students_number       INTEGER      NOT NULL,
    operating_system      VARCHAR(255),
    additional_equipment  VARCHAR(255),
    foreign key (degree_id) references degree (id),
    foreign key (subject_id) references subject (id),
    foreign key (semester_id) references semester (id),
    foreign key (professor_id) references professor (id),
    foreign key (responsible_id) references responsible (id),
    foreign key (department_id) references department (id)
);

CREATE INDEX idx_public_id ON reservation (public_id);
