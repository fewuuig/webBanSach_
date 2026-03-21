package com.example.webbansach_backend.Enum;

public enum RoleRoomChat {
    ADMIN("chủ phong chat") ,
    MEMBER("thành viên của đoạn chat") ;

    private String moTa ;
    public String getMoTa() {
        return moTa;
    }
    RoleRoomChat(String moTa ){
        this.moTa = moTa ;
    }
}
