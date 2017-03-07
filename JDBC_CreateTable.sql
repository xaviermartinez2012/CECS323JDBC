create table writinggroups (
    groupname  varchar(20) not null,
    headwriter varchar(20) not null,
    yearformed varchar(20) not null,
    subject    varchar(20) not null,
    constraint writinggroups_pk
    primary key (groupname)
);

create table publishers (
    publishername    varchar(20) not null,
    publisheraddress varchar(20) not null,
    publisherphone   varchar(20) not null,
    publisheremail   varchar(20) not null,
    constraint publishers_pk
    primary key (publishername)
);

create table books (
    groupname     varchar(20) not null,
    booktitle     varchar(20) not null,
    publishername varchar(20) not null,
    yearpublished varchar(20) not null,
    numberpages   varchar(20) not null,
    constraint books_pk
    primary key (groupname, booktitle),
    constraint writinggroups_fk
    foreign key (groupname)
    references writinggroups (groupname),
    constraint publishers_fk
    foreign key (publishername)
    references publishers(publishername),
    constraint books_uk1
    unique (booktitle, publishername)
);