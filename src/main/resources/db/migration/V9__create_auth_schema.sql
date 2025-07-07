create table auth_local (
        user_id char(36) primary key,
        email varchar(50) unique not null,
        password_hash varchar(255) not null,
        status int default 1,
        created_at datetime default current_timestamp,
        updated_at datetime default current_timestamp ON UPDATE current_timestamp,
        foreign key (user_id) references users(user_id)
);

create table auth_oauth2 (
         id int primary key auto_increment,
         user_id char(36) not null,
         provider enum("Google","Github") default "Google",
         provider_user_id varchar(100) not null,
         email varchar(100),
         status int default 1,
         created_at datetime default current_timestamp,
         updated_at datetime default current_timestamp on update current_timestamp,
         unique (provider, provider_user_id),
         foreign key (user_id) references users(user_id)
);

