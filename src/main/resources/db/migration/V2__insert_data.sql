INSERT INTO topics(name) VALUES ('Array');
INSERT INTO topics(name) VALUES ('Two pointer');

INSERT INTO difficulties(name) VALUES ('Hard');
INSERT INTO difficulties(name) VALUES ('Medium');
INSERT INTO difficulties(name) VALUES ('Easy');

INSERT INTO programming_languages (name, version, file_name)
VALUES
    ('C', '10.2.0', 'c'),
    ('C++', '10.2.0', 'cpp'),
    ('JavaScript', '15.10.0', 'js');

ALTER TABLE programming_languages ADD COLUMN file_main_name VARCHAR(10);

UPDATE programming_languages
SET file_main_name = CASE file_name
                         WHEN 'cpp' THEN 'main.cpp'
                         WHEN 'c'   THEN 'main.c'
                         WHEN 'js'  THEN 'index.js'
                         ELSE file_main_name
    END
WHERE file_name IN ('cpp', 'c', 'js');

