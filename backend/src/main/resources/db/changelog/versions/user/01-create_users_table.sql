-- create type mode as enum ('CHILD', 'ADULT');
-- create type role as enum ('USER', 'ADMIN');

create table users
(
    user_id    bigint primary key,
    username   varchar(128)                                           not null,
    age        int                                                    not null check ( 0 <= age and age <= 150 ),
    mode       varchar(16) check ( mode = 'CHILD' or mode = 'ADULT' ) not null default 'CHILD',
    experience int                                                    not null default 0,
    role       varchar(16) check ( role = 'USER' or role = 'ADMIN' )  not null default 'USER',
    created_at timestamp                                              not null default current_timestamp
);