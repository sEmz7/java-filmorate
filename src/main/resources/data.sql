MERGE INTO ratings AS r
    USING (VALUES
               ('G'),
               ('PG'),
               ('PG-13'),
               ('R'),
               ('NC-17')
        ) AS vals(name)
ON r.name = vals.name
WHEN NOT MATCHED THEN
    INSERT (name) VALUES (vals.name);

MERGE INTO genres AS g
    USING (VALUES
               ('Комедия'),
               ('Драма'),
               ('Мультфильм'),
               ('Триллер'),
               ('Документальный'),
               ('Боевик')
        ) AS vals(name)
ON g.name = vals.name
WHEN NOT MATCHED THEN
    INSERT (name) VALUES (vals.name);
