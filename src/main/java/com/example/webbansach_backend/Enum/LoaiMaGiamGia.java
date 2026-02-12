package com.example.webbansach_backend.Enum;

public enum LoaiMaGiamGia {
    PHAN_TRAM("Giam theo phần trăm") ,
    TIEN("Giảm theo tiền") ;

    private String moTa ;

    public String getMaTa(){
        return moTa ; 
    }

    LoaiMaGiamGia(String moTa ){
        this.moTa = moTa ;
    }
}
