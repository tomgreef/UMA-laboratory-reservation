CREATE TABLE reservation_assignment
(
    id             SERIAL PRIMARY KEY,
    reservation_id INT NOT NULL,
    laboratory_id  INT NOT NULL,
    semester_id    INT NOT NULL,
    FOREIGN KEY (reservation_id) REFERENCES reservation (id),
    FOREIGN KEY (laboratory_id) REFERENCES laboratory (id),
    FOREIGN KEY (semester_id) REFERENCES semester (id)
);