package com.example.webbansach_backend.Enum;

public enum CheckAccount {
    USERNAME("check username exists") ,
    EMAIL("Check password exists");

    private String mota ;

    public String getMoTa() {
        return this.mota;
    }
    CheckAccount(String mota ){
        this.mota = mota ;
    }
}
