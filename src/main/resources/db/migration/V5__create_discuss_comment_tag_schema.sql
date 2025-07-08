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
      foreign key (discuss_id) references discuss(discuss_id) on delete cascade ,
      foreign key (parent_id) references comments(comment_id) on delete cascade
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
      foreign key (tag_id) references tags(tag_id) on delete cascade ,
      foreign key (discuss_id) references discuss(discuss_id) on delete cascade
);

create table solutions (
       solution_id int primary key auto_increment,
       discuss_id int not null,
       problem_id int not null,
       status int default 1,
       created_at datetime default current_timestamp,
       updated_at datetime default current_timestamp,
       unique (discuss_id, problem_id),
       foreign key (discuss_id) references discuss(discuss_id) on delete cascade ,
       foreign key (problem_id) references problems(problem_id) on delete cascade
);