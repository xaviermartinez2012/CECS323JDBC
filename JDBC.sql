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

insert into publishers (publishername, publisheraddress, publisherphone, publisheremail)
    values('Burnin Books', 'Long Beach', '619-222-9999', 'burninbooks@mail.com');
insert into publishers (publishername, publisheraddress, publisherphone, publisheremail)
    values('Royal', 'Seattle', '455-422-9139', 'royal@mail.com');
insert into publishers (publishername, publisheraddress, publisherphone, publisheremail)
    values('Knight', 'San Diego', '619-122-2459', 'knight@mail.com');
insert into publishers (publishername, publisheraddress, publisherphone, publisheremail)
    values('Feminist Publishing', 'San Francisco', '858-123-4567', 'fem.pub@mail.com');
insert into publishers (publishername, publisheraddress, publisherphone, publisheremail)
    values('For Men', 'Texas', '222-365-3655', 'For.Men@mail.com');
insert into publishers (publishername, publisheraddress, publisherphone, publisheremail)
    values('Incredible', 'New York', '111-111-1111', 'incredible@mail.com');

insert into writinggroups (groupname, headwriter, yearformed, subject)
    values ('The Flaming Pencils', 'Xavier Martinez', '2017', 'Fictional Fiction');
insert into writinggroups (groupname, headwriter, yearformed, subject)
    values ('The Cool Fingers', 'Brian Lombardo', '2017', 'Fictional Fiction');
insert into writinggroups (groupname, headwriter, yearformed, subject)
    values ('Educationalists', 'Johnny Peacock', '1999', 'Education');
insert into writinggroups (groupname, headwriter, yearformed, subject)
    values ('Flying Papers', 'Anna Michael', '2000', 'Comedy');
insert into writinggroups (groupname, headwriter, yearformed, subject)
    values ('Team Slytherin', 'Parry Hotter', '2005', 'Sorcery Critique');
insert into writinggroups (groupname, headwriter, yearformed, subject)
    values ('Team Griffindor', 'Vord Loldemort', '2005', 'Sorcery Critique');
insert into writinggroups (groupname, headwriter, yearformed, subject)
    values ('The Dudes', 'Shannon Soriano', '2016', 'Political Analysis');
insert into writinggroups (groupname, headwriter, yearformed, subject)
    values ('The Girls', 'Bill Clinton', '2015', 'Romance');

insert into books (groupname, booktitle, publishername, yearpublished, numberpages)
    values('Educationalists', 'Learning Python', 'Royal', '2016', '315');
insert into books (groupname, booktitle, publishername, yearpublished, numberpages)
    values('Educationalists', 'Learning SQL', 'Royal', '2012', '550');
insert into books (groupname, booktitle, publishername, yearpublished, numberpages)
    values('Team Griffindor', 'Finding Voldermort', 'Knight', '2013', '400');
insert into books (groupname, booktitle, publishername, yearpublished, numberpages)
    values('Team Griffindor', 'Finding Harmione', 'Knight', '2014', '210');
insert into books (groupname, booktitle, publishername, yearpublished, numberpages)
    values('The Dudes', 'What Guys Do.', 'For Men', '2017', '99');
insert into books (groupname, booktitle, publishername, yearpublished, numberpages)
    values('The Dudes', 'What Guys Dont Do.', 'For Men', '2017', '98');
insert into books (groupname, booktitle, publishername, yearpublished, numberpages)
    values('The Girls', 'What Guys Do.', 'Feminist Publishing', '2017', '50');
insert into books (groupname, booktitle, publishername, yearpublished, numberpages)
    values('The Girls', 'What Guys Dont Do.', 'Feminist Publishing', '2017', '1000');