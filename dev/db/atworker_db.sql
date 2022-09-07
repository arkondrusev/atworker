create table worker_log (timestamp integer not null, tag text, msg text not null);

create index worklog_tmstmp_ix on worker_log (timestamp);

create table at_price (timestamp integer not null, pair text not null, best_ask real not null, best_bid real not null);

create index atprice_tmstmp_ix on at_prices (timestamp);

create table worker_state (key text unique not null, value text);

create index workstate_key_ix on worker_state (key);

/*
create table at_profit (timestamp integer not null, route text not null, profit_pct integer not null);
*/

create table stock_exchange (
	se_id integer primary key not null,
	se_name text not null,
	trade_fee real not null
	);
	
create table token (
	token_id integer primary key not null,
	token_name text not null
	);
	
create table token_se (
	token_id not null,
	se_id not null,
	token_se_name text
	);	
	
create table pair (
	pair_id integer primary key not null,
	upper_token_id integer,
	bottom_token_id integer
	);
	
create table pair_se (
	pair_id integer not null,
	se_id integer not null
	);

create table route (
	route_id integer primary key not null
	);	
	
create table route_pair (
	route_pair_id integer primary key not null,
	route_id integer not null,
	pair_id integer not null
	);

create table route_se (
	route_se_id integer primary key not null,
	route_id integer not null,
	se_id integer not null);

create table profit_moment (
	profit_moment_id integer primary key not null,
	route_se_id integer not null,
	timestamp integer not null,
	route_direction integer not null,
	profit_pct real not null
	);