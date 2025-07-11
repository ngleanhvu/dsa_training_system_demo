create database dsa_training_system;

use dsa_training_system;

create table auths (
	user_id CHAR(36) PRIMARY KEY,
    email varchar(50) not null unique,
    password varchar(255) null,
    role enum('admin', 'user') default 'user',
    status int default 1,
    created_at datetime default current_timestamp,
    updated_at datetime default current_timestamp on update current_timestamp,
    foreign key (user_id) references users(user_id)
);

create table users (
	user_id char(36) primary key,
    display_name varchar(50),
    email varchar(100) not null unique,
    avatar_url varchar(100) default '',
    first_name varchar(50),
    last_name varchar(50),
    date_of_birth date,
    gender enum('male','female','other') not null default 'other',
    role enum('admin','user') default 'user',
	phone_number varchar(10),
    address varchar(100),
    github varchar(255),
    linkedin_url varchar(255),
    total_solved int default 0,
    easy_solved int default 0,
    medium_solved int default 0,
    hard_solved int default 0,
    ranking_score int default 0,
    status int default 1,
    created_at datetime default current_timestamp,
    updated_at datetime default current_timestamp on update current_timestamp
);

create table external_auth_providers (
	external_auth_provider_id int primary key auto_increment,
    name varchar(50) not null unique,
    enpoint varchar(100),
    status int default 1,
    created_at datetime default current_timestamp,
    updated_at datetime default current_timestamp on update current_timestamp
);

create table external_auths (
	external_auth_id int primary key auto_increment,
    user_id varchar(36) not null unique,
    external_auth_provider_id int not null,
    auth_token varchar(255),
    status int default 1,
    created_at datetime default current_timestamp,
    updated_at datetime default current_timestamp on update current_timestamp,
    foreign key (user_id) references users(user_id),
    foreign key (external_auth_provider_id) references external_auth_providers(external_auth_provider_id),
    unique (user_id, external_auth_provider_id)
);

create table topics (
	topic_id int primary key auto_increment,
    name varchar(100) not null unique,
    description varchar(255),
    color_code varchar(7),
    status int default 1,
    created_at datetime default current_timestamp,
    updated_at datetime default current_timestamp on update current_timestamp
);

create table difficulties (
	difficulty_id int primary key auto_increment,
    name varchar(50) not null unique,
	status int default 1,
    created_at datetime default current_timestamp,
    updated_at datetime default current_timestamp on update current_timestamp
);

create table problems (
	problem_id int primary key auto_increment,
    title varchar(255),
    slug VARCHAR(200),
    difficulty_id int not null,
    acceptance_rate decimal(5,2) default 0.00,
    topic_id int not null,
    status int default 1,
    created_at datetime default current_timestamp,
    updated_at datetime default current_timestamp on update current_timestamp,
    foreign key (topic_id) references topics(topic_id),
    foreign key (difficulty_id) references difficulties(difficulty_id)
);

create table problem_details (
	problem_detail_id int primary key auto_increment,
    description text,
    constraints text,
    hints json,
    time_limit int default 1000, -- miliseconds
    memory_limit int default 256, -- MB
    problem_id int not null unique,
    status int default 1,
    created_at datetime default current_timestamp,
    updated_at datetime default current_timestamp on update current_timestamp,
    foreign key (problem_id) references problems(problem_id)
);

create table exambles (
	example_id int primary key auto_increment,
    input JSON null,
    output JSON not null,
	expantation text,
    problem_id int not null,
    status int default 1,
    created_at datetime default current_timestamp,
    updated_at datetime default current_timestamp on update current_timestamp,
    foreign key (problem_id) references problems(problem_id)
);

create table problems_topics (
	problem_topic_id int primary key auto_increment,
    problem_id int not null,
    topic_id int not null,
    status int default 1,
    created_at datetime default current_timestamp,
    updated_at datetime default current_timestamp on update current_timestamp,
    foreign key (problem_id) references problems(problem_id),
    foreign key (topic_id) references topics(topic_id)
);

create table test_cases (
	test_case_id int primary key auto_increment,
    input JSON,
    output JSON,
    expected_output JSON,
    problem_id int not null,
	status int default 1,
    created_at datetime default current_timestamp,
    updated_at datetime default current_timestamp on update current_timestamp,
    foreign key (problem_id) references problems(problem_id)
);

create table programming_languages (
	language_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    version VARCHAR(20),
    file_extension VARCHAR(10),
    is_active BOOLEAN DEFAULT TRUE,
    status int default 1,
    created_at datetime default current_timestamp,
    updated_at datetime default current_timestamp on update current_timestamp
);

create table problem_status (
	problem_status_id int primary key auto_increment,
    name varchar(20),
    status int default 1,
    created_at datetime default current_timestamp,
    updated_at datetime default current_timestamp on update current_timestamp
);

CREATE TABLE submissions (
    submission_id int primary key auto_increment,
    user_id varchar(36) not null,
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
    foreign key (user_id) references users(user_id),
    foreign key (problem_id) references problems(problem_id),
	foreign key(programming_language_id) references programming_languages(programming_language_id)
);

create table user_problem_status (
	user_problem_status_id int primary key auto_increment,
    user_id varchar(36) not null,
    problem_id int not null,
    problem_status enum('Accepted', 'Attempted') null,
    best_submission_id int,
    attempts int default 1,
    first_solved_at  datetime null,
    last_attempt_at datetime default current_timestamp,
    status int default 1,
    created_at datetime default current_timestamp,
    updated_at datetime default current_timestamp on update current_timestamp,
    foreign key (user_id) references users(user_id),
    foreign key (problem_id) references problems(problem_id),
    foreign key (best_submission_id) references submissions(submission_id),
    unique(user_id, problem_id)
);

create table discuss (
    discuss_id int primary key auto_increment,
    title varchar(255) not null,
    content TEXT not null,
    views int default 0,
    up_votes int default 0,
    down_votes int default 0,
    status int default 1,
    created_at datetime default current_timestamp,
    updated_at datetime default current_timestamp
);

create table comments (
    comment_id int primary key auto_increment,
    content text not null,
    discuss_id int not null,
    parent_id int default null,
    up_votes int default 0,
    down_votes int default 0,
    status int default 1,
    created_at datetime default current_timestamp,
    updated_at datetime default current_timestamp,
    foreign key (discuss_id) references discuss(discuss_id),
    foreign key (parent_id) references comments(comment_id)
);

create table tags (
    tag_id int primary key auto_increment,
    name varchar(50) not null unique,
    status int default 1,
    created_at datetime default current_timestamp,
    updated_at datetime default current_timestamp
);

create table discuss_tags (
    discuss_tag_id int primary key auto_increment,
    tag_id int not null,
    discuss_id int not null,
    unique (tag_id, discuss_id),
    status int default 1,
    created_at datetime default current_timestamp,
    updated_at datetime default current_timestamp,
    foreign key (tag_id) references tags(tag_id),
    foreign key (discuss_id) references discuss(discuss_id)
);

create table solutions (
    solution_id int primary key auto_increment,
    discuss_id int not null,
    problem_id int not null,
    status int default 1,
    created_at datetime default current_timestamp,
    updated_at datetime default current_timestamp,
    unique (discuss_id, problem_id),
    foreign key (discuss_id) references discuss(discuss_id),
    foreign key (problem_id) references problems(problem_id)
);

CREATE TABLE contests (
                          contest_id INT PRIMARY KEY AUTO_INCREMENT,
                          title VARCHAR(255) NOT NULL,
                          slug VARCHAR(255) UNIQUE NOT NULL,
                          description TEXT,
                          start_time DATETIME NOT NULL,
                          duration_minutes INT DEFAULT 90,
                          is_rated BOOLEAN DEFAULT TRUE,
                          status ENUM('UPCOMING', 'ONGOING', 'FINISHED') DEFAULT 'UPCOMING',
                          created_by VARCHAR(36),
                          created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                          updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                          FOREIGN KEY (created_by) REFERENCES users(user_id)
);

CREATE TABLE contest_problems (
                                  contest_problem_id INT PRIMARY KEY AUTO_INCREMENT,
                                  contest_id INT NOT NULL,
                                  problem_id INT NOT NULL,
                                  score INT DEFAULT 100,
                                  order_index INT DEFAULT 1,
                                  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                                  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                  FOREIGN KEY (contest_id) REFERENCES contests(contest_id),
                                  FOREIGN KEY (problem_id) REFERENCES problems(problem_id),
                                  UNIQUE (contest_id, problem_id)
);


CREATE TABLE contest_participants (
                                      id INT PRIMARY KEY AUTO_INCREMENT,
                                      contest_id INT NOT NULL,
                                      user_id VARCHAR(36) NOT NULL,
                                      joined_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                                      status ENUM('joined', 'disqualified', 'finished') DEFAULT 'joined',
                                      FOREIGN KEY (contest_id) REFERENCES contests(contest_id),
                                      FOREIGN KEY (user_id) REFERENCES users(user_id),
                                      UNIQUE (contest_id, user_id)
);


CREATE TABLE contest_submissions (
                                     contest_submission_id INT PRIMARY KEY AUTO_INCREMENT,
                                     submission_id INT NOT NULL UNIQUE,
                                     contest_id INT NOT NULL,
                                     user_id VARCHAR(36) NOT NULL,
                                     problem_id INT NOT NULL,

    -- Optionally copy lại thông tin cho truy xuất nhanh (denormalized)
                                     score INT DEFAULT 0,
                                     is_accepted BOOLEAN DEFAULT FALSE,
                                     submitted_at DATETIME DEFAULT CURRENT_TIMESTAMP,

                                     status INT DEFAULT 1,
                                     created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                                     updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                                     FOREIGN KEY (submission_id) REFERENCES submissions(submission_id),
                                     FOREIGN KEY (contest_id) REFERENCES contests(contest_id),
                                     FOREIGN KEY (user_id) REFERENCES users(user_id),
                                     FOREIGN KEY (problem_id) REFERENCES problems(problem_id)
);

CREATE TABLE contest_rankings (
                                  ranking_id INT PRIMARY KEY AUTO_INCREMENT,
                                  contest_id INT NOT NULL,
                                  user_id VARCHAR(36) NOT NULL,
                                  total_score INT DEFAULT 0,
                                  total_time_seconds INT DEFAULT 0,
                                  last_submission_at DATETIME,
                                  rank INT DEFAULT NULL,
                                  FOREIGN KEY (contest_id) REFERENCES contests(contest_id),
                                  FOREIGN KEY (user_id) REFERENCES users(user_id),
                                  UNIQUE (contest_id, user_id)
);





