SET referential_integrity FALSE;
TRUNCATE TABLE film_genres;
TRUNCATE TABLE genres RESTART IDENTITY;
TRUNCATE TABLE likes;
TRUNCATE TABLE friends;
TRUNCATE TABLE status RESTART IDENTITY;
TRUNCATE TABLE ratings RESTART IDENTITY;
TRUNCATE TABLE users RESTART IDENTITY;
TRUNCATE TABLE films RESTART IDENTITY;
SET referential_integrity TRUE;

INSERT INTO ratings(name)
VALUES ('G'),
       ('PG'),
       ('PG-13'),
       ('R'),
       ('NC-17');

INSERT INTO genres (name)
VALUES ('Комедия'),
       ('Драма'),
       ('Мультфильм'),
       ('Триллер'),
       ('Документальный'),
       ('Боевик');