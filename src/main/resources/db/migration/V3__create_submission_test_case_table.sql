CREATE TABLE IF NOT EXISTS submissions_test_cases (
    id INT PRIMARY KEY AUTO_INCREMENT,
    submission_id INT,
    test_case_id INT,
    submission_status ENUM('Pending', 'Accepted', 'Wrong Answer', 'Time Limit Exceeded',
        'Memory Limit Exceeded', 'Runtime Error', 'Compile Error') DEFAULT 'Pending',
    status int default 1,
    created_at datetime default current_timestamp,
    updated_at datetime default current_timestamp on update current_timestamp,
    foreign key (submission_id) references submissions(submission_id) on delete cascade,
    foreign key (test_case_id) references test_cases(test_case_id) on delete cascade
)