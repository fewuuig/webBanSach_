package com.example.webbansach_backend.Enum;

public enum MessageStatus {
    SENDED("đã gửi") ,
    ERROR("không gửi đc") ;

    private String moTa ;
    public String getMoTa() {
        return moTa;
    }
    MessageStatus(String moTa ){
        this.moTa = moTa ;
    }
}
