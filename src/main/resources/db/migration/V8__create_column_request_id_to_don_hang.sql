ALTER TABLE don_hang
ADD  COLUMN request_id varchar(100) ;
Alter TABLE don_hang
ADD constraint uk_request_id unique (request_id) ;
