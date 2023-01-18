CREATE TABLE if not exists down_load_log
(
    ID          BIGINT PRIMARY KEY AUTO_INCREMENT,
    file_name   VARCHAR(100),
    orgin_url   VARCHAR(500),
    local_url   VARCHAR(200),
    status      varchar(10),
    submit_date timestamp,
    complete_date timestamp,
    user_ip     varchar(64),
    user_agent  varchar(200)
);
-- delete from down_load_log where 1=1;