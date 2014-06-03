create table users (
	id serial primary key,
	name text not null unique,
	k_name text not null,
	password text not null
);

create table sessions (
	id text primary key,
	user_id int references users (id),
	date timestamp not null default now()
);

create table cart (
	id serial primary key,
	session_id text not null references sessions (id) on delete cascade,
	color text not null
);

create table sales (
	id serial primary key,
	user_id int not null references users (id),
	date timestamp not null default now()
);

create table sales_detail (
	sales_id int references sales (id),
	detail_id int,
	color text not null,
	primary key (sales_id, detail_id)
);
