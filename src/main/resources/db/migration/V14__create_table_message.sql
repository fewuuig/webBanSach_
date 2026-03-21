CREATE TABLE message(
    message_id bigint AUTO_INCREMENT not null primary key  ,
    content LONGTEXT not null  ,
    type varchar(10) not null ,
    created_at TIMESTAMP default  CURRENT_TIMESTAMP,
    room_id int not null ,
    ma_nguoi_dung int not null ,
    status varchar(10) not null ,
    CONSTRAINT FK_message_nguoi_dung FOREIGN KEY (ma_nguoi_dung) REFERENCES nguoi_dung(ma_nguoi_dung) ,
    CONSTRAINT FK_message_room Foreign Key (room_id) REFERENCES room(room_id)
)