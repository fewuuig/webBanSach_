package com.example.webbansach_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


public class DiaChiGiaoHangResponeDTO {
    private int  maDiaChi ;
    private String tinhOrCity ;
    private String quanOrHuyen ;
    private String phuongOrXa ;
    private String soNha ;

    public DiaChiGiaoHangResponeDTO(Builder builder){
        this.tinhOrCity = builder.tinhOrCity;
        this.quanOrHuyen = builder.quanOrHuyen; ;
        this.phuongOrXa  =builder.phuongOrXa ;
        this.soNha = builder.soNha;
        this.maDiaChi = builder.maDiaChi ;
    }
    public String getTinhOrCity() {
        return tinhOrCity;
    }

    public String getQuanOrHuyen() {
        return quanOrHuyen;
    }

    public String getPhuongOrXa() {
        return phuongOrXa;
    }

    public String getSoNha() {
        return soNha;
    }

    public int getMaDiaChi() {
        return maDiaChi;
    }

    public static class Builder{
        private int maDiaChi ;
        private String tinhOrCity ;
        private String quanOrHuyen ;
        private String phuongOrXa ;
        private String soNha ;

        public Builder setTinhOrCity(String tinhOrCity) {
            this.tinhOrCity = tinhOrCity;
            return this;
        }

        public Builder setQuanOrHuyen(String quanOrHuyen) {
            this.quanOrHuyen = quanOrHuyen;
            return this;
        }

        public Builder setPhuongOrXa(String phuongOrXa) {
            this.phuongOrXa = phuongOrXa;
            return this;
        }

        public Builder setSoNha(String soNha) {
            this.soNha = soNha;
            return this;
        }

        public Builder setMaDiaChi(int maDiaChi) {
            this.maDiaChi = maDiaChi;
            return this;
        }

        public DiaChiGiaoHangResponeDTO build(){
            return new DiaChiGiaoHangResponeDTO(this) ;
        }
    }

}
