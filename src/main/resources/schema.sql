drop table if exists book;
create table book(
id integer primary key auto_increment,
title varchar(50) not null,
description varchar(4096) not null,
image varchar(200),
author varchar(50) not null,
published Date,
favorite integer default 0
);
drop table if exists member;
create table member(
    id integer primary key auto_increment,
    name varchar(50) not null,
    email varchar(100) not null unique,
    password varchar(150) not null
);
drop table if exists member_role;
create table member_role(
    id integer primary key  auto_increment,
    memberId integer not null,
    role_code int not null, --0: USER 1:ADMIN
    foreign key (memberId)references member(id)
);
