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
