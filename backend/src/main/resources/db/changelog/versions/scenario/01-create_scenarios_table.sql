create table types
(
    type_id uuid primary key default gen_random_uuid(),
    title   varchar(32)
);

create table scenarios
(
    scenario_id uuid primary key default gen_random_uuid(),
    title       varchar(64)   not null,
    text        varchar(4096) not null,
    scam        bool          not null,
    type_id     uuid          references types (type_id) on DELETE set null
);