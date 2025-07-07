create table users (
       user_id char(36) primary key,
       email varchar(100) not null unique,
       display_name varchar(50) not null,
       avatar varchar(255),
       role enum('admin','user') default 'user',
       status int default 1,
       created_at datetime default current_timestamp,
       updated_at datetime default current_timestamp on update current_timestamp
);

create table user_details (
      user_id char(36) primary key,
      first_name varchar(50),
      last_name varchar(50),
      date_of_birth date,
      gender enum('male','female','other') not null default 'other',
      phone_number varchar(15) unique ,
      address varchar(100),
      github_url varchar(255) unique,
      linkedin_url varchar(255) unique ,
      status int default 1,
      created_at datetime default current_timestamp,
      updated_at datetime default current_timestamp on update current_timestamp,
      foreign key (user_id) references users(user_id)
);

CREATE TABLE user_statistics (
     user_id CHAR(36) PRIMARY KEY,
     ranking_score INT DEFAULT 0,
     views int default 0,
     solutions int default 0,
     acceptance_rate double default 0,
     reputation int default 0,
     submission int default 0,
     discuss int default 0,
     status int default 1,
     languages JSON,
     created_at datetime default current_timestamp,
     updated_at datetime default current_timestamp on update current_timestamp,
     foreign key (user_id) references users(user_id)
);