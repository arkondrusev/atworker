create table worker_log (timestamp integer not null, tag text, msg text not null);

create index worklog_tmstmp_ix on worker_log (timestamp);

create table at_prices (timestamp integer not null, pair text not null, best_ask real not null, best_bid real not null);

create index atprices_tmstmp_ix on at_prices (timestamp);

create table worker_state (key text unique not null, value text);

create index workstate_key_ix on worker_state (key);