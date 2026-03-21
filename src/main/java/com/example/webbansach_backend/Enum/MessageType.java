package com.example.webbansach_backend.Enum;

public enum MessageType {
    TEXT("văn bản") ,
    IMAGE("hình ảnh") ;

    private String moTa ;
    public String getMoTa() {
        return moTa;
    }
    MessageType(String moTa ){
        this.moTa = moTa ;
    }
}
