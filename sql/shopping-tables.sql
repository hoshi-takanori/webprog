create table sessions (
	id text primary key,
	date timestamp not null default now()
);

create table cart (
	id serial primary key,
	session_id text not null references sessions (id) on delete cascade,
	color text not null
);
