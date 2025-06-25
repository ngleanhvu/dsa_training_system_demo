
create table if not exists topics (
              topic_id int primary key auto_increment,
              name varchar(100) not null unique,
              description varchar(255),
              color_code varchar(7),
              status int default 1,
              created_at datetime default current_timestamp,
              updated_at datetime default current_timestamp on update current_timestamp
);

create table if not exists difficulties (
              difficulty_id int primary key auto_increment,
              name varchar(50) not null unique,
              status int default 1,
              created_at datetime default current_timestamp,
              updated_at datetime default current_timestamp on update current_timestamp
);

create table if not exists problems (
              problem_id int primary key auto_increment,
              title varchar(255),
              slug VARCHAR(200),
              difficulty_id int not null,
              acceptance_rate decimal(5,2) default 0.00,
              status int default 1,
              created_at datetime default current_timestamp,
              updated_at datetime default current_timestamp on update current_timestamp,
              foreign key (difficulty_id) references difficulties(difficulty_id)
);

create table if not exists problem_details (
              problem_detail_id int primary key auto_increment,
              description text,
              constraints json,
              hints json,
              time_limit int default 1000, -- miliseconds
              memory_limit int default 256, -- MB
              problem_id int not null unique,
              status int default 1,
              created_at datetime default current_timestamp,
              updated_at datetime default current_timestamp on update current_timestamp,
              foreign key (problem_id) references problems(problem_id)
);

create table if not exists examples (
             example_id int primary key auto_increment,
             images  JSON,
             input text null,
             output text not null,
             explantation text,
             problem_id int not null,
             status int default 1,
             created_at datetime default current_timestamp,
             updated_at datetime default current_timestamp on update current_timestamp,
             foreign key (problem_id) references problems(problem_id)
);

create table if not exists problems_topics (
             problem_topic_id int primary key auto_increment,
             problem_id int not null,
             topic_id int not null,
             status int default 1,
             created_at datetime default current_timestamp,
             updated_at datetime default current_timestamp on update current_timestamp,
             foreign key (problem_id) references problems(problem_id),
             foreign key (topic_id) references topics(topic_id),
             unique (topic_id, problem_id)
);

create table if not exists test_cases (
            test_case_id int primary key auto_increment,
            input text,
            output text,
            problem_id int not null,
            status int default 1,
            created_at datetime default current_timestamp,
            updated_at datetime default current_timestamp on update current_timestamp,
            foreign key (problem_id) references problems(problem_id)
);

create table if not exists programming_languages (
            programming_language_id INT PRIMARY KEY AUTO_INCREMENT,
            name VARCHAR(50) NOT NULL,
            version VARCHAR(20),
            file_extension VARCHAR(10),
            is_active BOOLEAN DEFAULT TRUE,
            status int default 1,
            created_at datetime default current_timestamp,
            updated_at datetime default current_timestamp on update current_timestamp
);

CREATE TABLE if not exists submissions (
            submission_id int primary key auto_increment,
            problem_id int not null,
            programming_language_id int not null,
            code TEXT NOT NULL,
            submission_status ENUM('Pending', 'Accepted', 'Wrong Answer', 'Time Limit Exceeded',
                 'Memory Limit Exceeded', 'Runtime Error', 'Compile Error') DEFAULT 'Pending',
            runtime_ms int,
            memory_kb int,
            test_cases_passed int default 0,
            total_test_cases int default 0,
            error_message text,
            submitted_at timestamp default current_timestamp,
            status int default 1,
            created_at datetime default current_timestamp,
            updated_at datetime default current_timestamp on update current_timestamp,
            foreign key (problem_id) references problems(problem_id),
            foreign key (programming_language_id) references programming_languages(programming_language_id)
);

