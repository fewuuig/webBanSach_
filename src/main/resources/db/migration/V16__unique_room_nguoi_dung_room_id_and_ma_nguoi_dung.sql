ALTER Table room_nguoi_dung
ADD CONSTRAINT uk_room_id_and_ma_nguoi_dung UNIQUE (room_id , ma_nguoi_dung) ;