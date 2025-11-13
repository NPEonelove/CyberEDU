create table achievements
(
    achievement_id uuid primary key default gen_random_uuid(),
    name           varchar(64)  not null,
    description    varchar(256) not null,
    icon           varchar(128),
    required_exp   integer      not null,
    created_at     timestamp        default current_timestamp
);

create table user_achievements
(
    user_id        bigint not null references users (user_id),
    achievement_id uuid   not null references achievements (achievement_id),
    earned_at      timestamp default current_timestamp,
    primary key (user_id, achievement_id)
);