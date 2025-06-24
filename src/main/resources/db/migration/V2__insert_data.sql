INSERT INTO topics(name) VALUES ('Array');
INSERT INTO topics(name) VALUES ('Two pointer');

INSERT INTO difficulties(name) VALUES ('Hard');
INSERT INTO difficulties(name) VALUES ('Medium');
INSERT INTO difficulties(name) VALUES ('Easy');

INSERT INTO programming_languages (name, version, file_extension)
VALUES
    ('C', 'GCC 12.2.0', '.c'),
    ('C++', 'G++ 12.2.0', '.cpp'),
    ('Java', 'OpenJDK 17', '.java'),
    ('C#', '.NET 7.0', '.cs'),
    ('JavaScript', 'Node.js 18.15.0', '.js'),
    ('Python', '3.10.0', '.py');

ALTER TABLE programming_languages ADD judge0_language_id INT;

UPDATE programming_languages SET judge0_language_id = 50 WHERE name = 'Java';        -- Java (OpenJDK 17)
UPDATE programming_languages SET judge0_language_id = 54 WHERE name = 'Python';      -- Python 3.10
UPDATE programming_languages SET judge0_language_id = 76 WHERE name = 'C++';         -- G++ 12.2.0
UPDATE programming_languages SET judge0_language_id = 75 WHERE name = 'C';           -- GCC 12.2.0
UPDATE programming_languages SET judge0_language_id = 92 WHERE name = 'C#';          -- .NET 7.0
UPDATE programming_languages SET judge0_language_id = 93 WHERE name = 'JavaScript';  -- Node.js 18
