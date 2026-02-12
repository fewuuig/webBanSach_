package com.example.webbansach_backend.Enum;

public enum TrangThaiMaGiamGia {
    DANG_HOAT_DONG("đang hoạt động") ,
    KHOA ("đã khóa"),
    HET_HAN("hết hạn") ;

    private String moTa ;

    public String getMoTa() {
        return moTa;
    }

    TrangThaiMaGiamGia(String moTa){
        this.moTa = moTa ;
    }

}
