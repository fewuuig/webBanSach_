package com.example.webbansach_backend.Enum;

public enum TrangThaiGiaoHang {
    CHO_XAC_NHAN("Chờ xác nhận") ,
    DA_XAC_NHAN("Đã xác nhận") ,
    DANG_GIAO("Đang giao") ,
    DA_GIAO("Đã giao") ,
    DA_HUY("Đã hủy");

    private final String moTa ;

    public String getMoTa() {
        return moTa;
    }

    TrangThaiGiaoHang(String moTa ){
        this.moTa = moTa ;
    }

}
