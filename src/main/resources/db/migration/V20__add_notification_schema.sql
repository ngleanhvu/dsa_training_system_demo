create table if not exists notifications (
    notification_id int primary key auto_increment,
    content varchar(255) not null,
    type ENUM ('ALERT','INFO','WARNING'),
    created_at timestamp default current_timestamp,
    updated_at timestamp default current_timestamp on update current_timestamp
);

create table notifications_users (
    notification_user_id int primary key auto_increment,
    is_read boolean default false,
    sender_id varchar(36) null,
    receiver_id varchar(36) not null,
    notification_id int not null,
    foreign key (sender_id) references users(user_id),
    foreign key (receiver_id) references users(user_id),
    foreign key (notification_id) references notifications(notification_id)
)

