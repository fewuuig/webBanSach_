package com.example.webbansach_backend.Enum;

public enum StatusLogOrder {
    PENDING("chờ xác nhận") ,
    CONFIRMED("đã xác nhận") ,
    CANCLED("đã bị hủy ");

    private String moTa ;
    StatusLogOrder(String moTa){
        this.moTa = moTa ;
    }

    public String getMoTa() {
        return moTa;
    }
}
