create table if not exists account
(
    id varchar(32),
    email varchar(128) not null,
    display_name varchar(128) not null,
    sso_id varchar(128),
    timestamp timestamp with time zone not null,

    constraint account_pk
        primary key (id),
    constraint account_u_displayname
        unique (display_name),
    constraint account_u_email
        unique (email)
);

create table if not exists department
(
    id varchar(32),
    name varchar(256) not null,
    owner_account_id varchar(32),
    timestamp timestamp with time zone not null,

    constraint department_pk
        primary key (id),
    constraint department_fk_owneraccountid
        foreign key (owner_account_id)
            references account
);

create table if not exists department_member
(
    department_id varchar(32),
    account_id varchar(32),

    constraint department_member_pk
        primary key (department_id, account_id),
    constraint department_member_fk_departmentid
        foreign key (department_id)
            references department
            on delete cascade,
    constraint department_member_fk_accountid
        foreign key (account_id)
            references account
            on delete cascade
);

create table if not exists foo
(
    id varchar(64),
    text_data varchar(128) not null,
    boolean_data boolean not null,
    version int not null,

    constraint foo_pk
        primary key (id)
);

create table if not exists bar
(
    id varchar(64),
    text_data varchar(128) not null,
    boolean_data boolean not null,
    timestamp timestamp with time zone not null,

    constraint bar_pk
        primary key (id)
);

create table if not exists corge
(
    id varchar(64),
    text_data varchar(128) not null,
    boolean_data boolean not null,
    tag varchar(128) not null,

    constraint corge_pk
        primary key (id)
);

create table if not exists quux
(
    id varchar(64),
    text_data varchar(128) not null,
    boolean_data boolean not null,
    created timestamp with time zone not null,
    updated timestamp with time zone not null,

    constraint quux_pk
        primary key (id)
);

create table if not exists fizzle
(
    id varchar(64),
    text_data varchar(128) not null,
    boolean_data boolean not null,

    constraint fizzle_pk
        primary key (id)
);

create table if not exists thud
(
    corge_id varchar(64) not null,
    quux_id varchar(64) not null,
    text_data varchar(128) not null,
    boolean_data boolean not null,
    timestamp timestamp with time zone not null,

    constraint thud_pk
        primary key (corge_id, quux_id)
);
