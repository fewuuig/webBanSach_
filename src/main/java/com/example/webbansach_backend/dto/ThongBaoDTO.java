package com.example.webbansach_backend.dto;

public class ThongBaoDTO {
    private String noiDung ;
    public ThongBaoDTO(String noiDung){
        this.noiDung = noiDung ;
    }

    public String getNoiDung() {
        return noiDung;
    }

    public void setNoiDung(String noiDung) {
        this.noiDung = noiDung;
    }
}
