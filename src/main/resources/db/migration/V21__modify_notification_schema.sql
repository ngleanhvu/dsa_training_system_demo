alter table notifications_users add column created_at timestamp default current_timestamp;
alter table notifications_users add column updated_at timestamp default current_timestamp on update current_timestamp;
alter table notifications_users add column status int default 1;
alter table notifications add column status int default 1;