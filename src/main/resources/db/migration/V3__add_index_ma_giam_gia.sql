-- đánh indexx cho maGiamGia và ngayHetHan : cái nào có dấu = khi truy vấn thì đặt trước , cái nào là phạm vi như : < > <= >= thì đặt sau
Create index  idx_tt_ma_giam_gia_and_ngay_het_han ON ma_giam_gia(trang_thai_ma_giam_gia , ngay_het_han) ;
-- không có chuyện index đánh theo cặp xuyên bảng , trong th lọc theo kiểu join bảng thì mk sẽ đánh ở 2 bảng khác nhau là ok
