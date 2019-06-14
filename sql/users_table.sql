create table users(
 id int not null primary key,
 username varchar(255) not null,
 password varchar(255) not null,
 image_url varchar(255),
 json_url varchar(255)
);
