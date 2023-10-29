create table post (
	id serial PRIMARY KEY,
	name VARCHAR(255),
	text TEXT,
	link TEXT UNIQUE,
	created TIMESTAMP
);