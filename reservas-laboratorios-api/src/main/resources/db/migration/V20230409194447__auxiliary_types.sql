create table degree
(
    id   SERIAL primary key,
    name VARCHAR(255) not null
);

create table subject
(
    id       SERIAL primary key,
    name     VARCHAR(255) not null,
    course   INT not null,
    "group"  VARCHAR(10)  not null,
    subgroup VARCHAR(50)  not null
);

create table semester
(
    id         SERIAL primary key,
    start_year INT                   not null,
    end_year   INT                   not null,
    period INT not null,
    is_active  BOOLEAN default FALSE not null,
    unique (start_year, end_year, period)
);

CREATE UNIQUE INDEX idx_active_semester
    ON semester (is_active) WHERE is_active = TRUE;


create table professor
(
    id    SERIAL primary key,
    name  VARCHAR(255) not null,
    email VARCHAR(255)
);

create table responsible
(
    id    SERIAL primary key,
    name  VARCHAR(255) not null,
    phone VARCHAR(20)
);

create table department
(
    id   SERIAL primary key,
    name VARCHAR(255) not null
);