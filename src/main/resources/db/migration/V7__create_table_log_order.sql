CREATE TABLE log_order
(
    log_order_id BIGINT not null auto_increment primary key  ,
    voucher_id   int,
    request_id   varchar(50),
    status varchar(15) ,
    create_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP  ,
    CONSTRAINT Uk_request_id UNIQUE (request_id)
);