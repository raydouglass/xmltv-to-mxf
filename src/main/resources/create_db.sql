create table if not exists series (
	identifier primary key,
	id text,
	uid text,
	title text,
	short_title text,
	description text,
	short_description text,
	start_airdate text,
	end_airdate text,
	guide_image text
);
    
create table if not exists season (
	id text primary key,
	uid text,
	series_uid text,
	title text,
	studio text,
	year text,
	guide_image text
);