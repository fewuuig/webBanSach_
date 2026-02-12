package com.example.webbansach_backend.Enum;

public enum DoiTuongApDungMa {
    NGUOI_DUNG("mã giảm giá dành cho người dùng") ,
    SACH("mã giảm giá dành cho sách") ,
    TOAN_HE_THONG ("tất cả các quyển sách"),
    THE_LOAI("áp dụng cho thể loại") ;

    private String moTa ;

    public String getMoTa() {
        return moTa;
    }

    DoiTuongApDungMa(String moTa ){
        this.moTa = moTa ;
    }

}
