create table worker_log (timestamp integer not null, tag text, msg text not null);

create index worklog_tmstmp_ix on worker_log (timestamp);