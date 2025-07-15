CREATE TABLE contests (
                          contest_id INT PRIMARY KEY AUTO_INCREMENT,
                          title VARCHAR(255) NOT NULL,
                          slug VARCHAR(255) UNIQUE NOT NULL,
                          description TEXT,
                          start_time DATETIME NOT NULL,
                          duration_minutes INT DEFAULT 90,
                          is_rated BOOLEAN DEFAULT TRUE,
                          contest_status ENUM('upcoming','ongoing', 'finished') DEFAULT 'upcoming',
                          status INT DEFAULT 1,
                          created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                          updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE contest_problems (
                                  contest_problem_id INT PRIMARY KEY AUTO_INCREMENT,
                                  contest_id INT NOT NULL,
                                  problem_id INT NOT NULL,
                                  score INT DEFAULT 100,
                                  order_index INT DEFAULT 1,
                                  status INT DEFAULT 1,
                                  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                                  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                                  FOREIGN KEY (contest_id) REFERENCES contests(contest_id)
                                      ON DELETE CASCADE,
                                  FOREIGN KEY (problem_id) REFERENCES problems(problem_id)
                                      ON DELETE CASCADE,

                                  UNIQUE (contest_id, problem_id)
);

CREATE TABLE contest_participants (
                                      id INT PRIMARY KEY AUTO_INCREMENT,
                                      contest_id INT NOT NULL,
                                      user_id VARCHAR(36) NOT NULL,
                                      status INT DEFAULT 1,
                                      created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                                      updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                                      FOREIGN KEY (contest_id) REFERENCES contests(contest_id)
                                          ON DELETE CASCADE,
                                      FOREIGN KEY (user_id) REFERENCES users(user_id)
                                          ON DELETE CASCADE,

                                      UNIQUE (contest_id, user_id)
);

CREATE TABLE contest_submissions (
                                     contest_submission_id INT PRIMARY KEY AUTO_INCREMENT,
                                     submission_id INT NOT NULL UNIQUE,
                                     contest_id INT NOT NULL,
                                     user_id VARCHAR(36) NOT NULL,
                                     problem_id INT NOT NULL,

                                     score INT DEFAULT 0,
                                     is_accepted BOOLEAN DEFAULT FALSE,
                                     submitted_at DATETIME DEFAULT CURRENT_TIMESTAMP,

                                     status INT DEFAULT 1,
                                     created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                                     updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                                     FOREIGN KEY (submission_id) REFERENCES submissions(submission_id)
                                         ON DELETE CASCADE,
                                     FOREIGN KEY (contest_id) REFERENCES contests(contest_id)
                                         ON DELETE CASCADE,
                                     FOREIGN KEY (user_id) REFERENCES users(user_id)
                                         ON DELETE CASCADE,
                                     FOREIGN KEY (problem_id) REFERENCES problems(problem_id)
                                         ON DELETE CASCADE
);
