CREATE TABLE laboratory
(
    id                   SERIAL PRIMARY KEY,
    name                 VARCHAR(255) NOT NULL,
    capacity             INTEGER      NOT NULL,
    location             VARCHAR(255) NOT NULL,
    operating_system     VARCHAR(255),
    additional_equipment VARCHAR(255)
);
create table adjacent_laboratory
(
    id                     serial primary key,
    laboratory_id          int not null,
    adjacent_laboratory_id int not null,
    foreign key (laboratory_id) references laboratory (id),
    foreign key (adjacent_laboratory_id) references laboratory (id)
);