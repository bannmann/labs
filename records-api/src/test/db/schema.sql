create table if not exists account
(
    id varchar(32) primary key,
    email varchar(128) not null unique,
    timestamp timestamp with time zone not null
);

create table if not exists password
(
    account_id varchar(32) not null primary key,
    digest varchar(256) not null,
    algorithm varchar(32) not null,
    timestamp timestamp with time zone not null,
    
    constraint password_account_id_fkey foreign key (account_id) references account(id) on delete cascade
);

create table if not exists foo
(
    id varchar(64) primary key,
    text_data varchar(128) not null,
    boolean_data boolean not null,
    version int not null
);

create table if not exists bar
(
    id varchar(64) primary key,
    text_data varchar(128) not null,
    boolean_data boolean not null,
    timestamp timestamp with time zone not null
);

create table if not exists corge
(
    id varchar(64) primary key,
    text_data varchar(128) not null,
    boolean_data boolean not null,
    tag varchar(128) not null
);

create table if not exists quux
(
    id varchar(64) primary key,
    text_data varchar(128) not null,
    boolean_data boolean not null,
    created timestamp with time zone not null,
    updated timestamp with time zone not null
);

create table if not exists fizzle
(
    id varchar(64) primary key,
    text_data varchar(128) not null,
    boolean_data boolean not null
);

create table if not exists thud
(
    corge_id varchar(64) not null,
    quux_id varchar(64) not null,
    text_data varchar(128) not null,
    boolean_data boolean not null,
    timestamp timestamp with time zone not null,
    primary key (corge_id, quux_id)
);
