create table chat_log (
	id serial primary key,
	date timestamp not null default now(),
	user_name text not null,
	message text not null
);
