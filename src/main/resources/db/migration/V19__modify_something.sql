ALTER TABLE discuss drop column problem_id;

CREATE TABLE problem_comments (
                                 comment_id INT PRIMARY KEY,
                                 problem_id INT NOT NULL,
                                 FOREIGN KEY (comment_id) REFERENCES comments(comment_id)
                                     ON DELETE CASCADE,
                                 FOREIGN KEY (problem_id) REFERENCES problems(problem_id)
                                     ON DELETE CASCADE
);