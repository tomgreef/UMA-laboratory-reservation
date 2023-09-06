CREATE TABLE reservation_conflict
(
    id              SERIAL PRIMARY KEY,
    reservation_id1 INT          NOT NULL,
    reservation_id2 INT          NOT NULL,
    start_date      DATE         NOT NULL,
    end_date        DATE         NOT NULL,
    day             VARCHAR(255) NOT NULL,
    start_time      TIME         NOT NULL,
    end_time        TIME         NOT NULL,
    laboratory_id   INT          NOT NULL,
    semester_id     INT          NOT NULL,
    FOREIGN KEY (reservation_id1) REFERENCES reservation (id),
    FOREIGN KEY (reservation_id2) REFERENCES reservation (id),
    FOREIGN KEY (laboratory_id) REFERENCES laboratory (id),
    FOREIGN KEY (semester_id) REFERENCES semester (id)
)