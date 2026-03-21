package com.example.webbansach_backend.Enum;

public enum RoomType {
    DM("chat 1-1") ,
    GROUP("chat nhom") ;

    private String moTa ;
    public String getMoTa() {
        return moTa;
    }
    RoomType(String moTa ){
        this.moTa = moTa ;
    }
}
