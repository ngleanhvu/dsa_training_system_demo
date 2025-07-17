alter table contests add column end_time datetime;

alter table contest_submissions drop column user_id;

alter table contest_submissions drop column problem_id;

alter table contest_problems drop column orderIndex;