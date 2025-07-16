alter table submissions add column submit_at datetime default current_timestamp;

alter table problems add column publish_at datetime default current_timestamp;
alter table problems add column is_public boolean default false