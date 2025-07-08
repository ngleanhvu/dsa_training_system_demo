create table if not exists discusses_votes (
    discuss_vote_id int primary key auto_increment,
    user_id char(36) not null,
    discuss_id int not null,
    unique (user_id, discuss_id),
    foreign key (user_id) references users(user_id) on delete cascade ,
    foreign key (discuss_id) references discuss(discuss_id) on delete cascade
);

create table if not exists comments_votes (
    comment_vote_id int primary key auto_increment,
    comment_id int not null,
    user_id char(36) not null,
    unique (comment_id, user_id),
    foreign key (comment_id) references comments(comment_id) on delete cascade ,
    foreign key (user_id) references users(user_id) on delete cascade
);

alter table comments drop column down_votes;
alter table discuss drop column down_votes;
alter table discuss add column comment_count int;
