CREATE  Table room_nguoi_dung(
    room_nguoi_dung_id int AUTO_INCREMENT Not Null PRIMARY KEY ,
    room_id int not null ,
    ma_nguoi_dung int not null ,
    role varchar(10) ,
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ,

    CONSTRAINT FK_room_user_room FOREIGN KEY (room_id) REFERENCES room(room_id) ,
    CONSTRAINT FK_room_user_user FOREIGN KEY (ma_nguoi_dung) REFERENCES nguoi_dung(ma_nguoi_dung)
);