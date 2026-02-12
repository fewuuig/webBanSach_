package com.example.webbansach_backend.dto;

public class ViewCartDTO {
    private Long maGioHangSach ;
    private int maSach ;
    private int soLuong ;
    private String tenSach ;
    private double giaBan ;
    private double tongGia ;

    public ViewCartDTO(Builder builder){
        this.maGioHangSach = builder.maGioHangSach;
        this.maSach = builder.maSach;
        this.soLuong = builder.soLuong;
        this.tenSach = builder.tenSach;

        this.giaBan = builder.giaBan;
        this.tongGia = builder.tongGia;
    }

    public Long getMaGioHangSach() {
        return maGioHangSach;
    }

    public int getMaSach() {
        return maSach;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public String getTenSach() {
        return tenSach;
    }


    public double getGiaBan() {
        return giaBan;
    }

    public double getTongGia() {
        return tongGia;
    }
    public static class Builder{
        private Long maGioHangSach ;
        private int maSach ;
        private int soLuong ;
        private String tenSach ;
        private double giaBan ;
        private double tongGia ;

        public Builder setMaGioHangSach(Long maGioHangSach) {
            this.maGioHangSach = maGioHangSach;
            return this;
        }

        public Builder setMaSach(int maSach) {
            this.maSach = maSach;
            return this;
        }

        public Builder setSoLuong(int soLuong) {
            this.soLuong = soLuong;
            return this;
        }

        public Builder setTenSach(String tenSach) {
            this.tenSach = tenSach;
            return this;
        }

        public Builder setGiaBan(double giaBan) {
            this.giaBan = giaBan;
            return this;
        }

        public Builder setTongGia(double tongGia) {
            this.tongGia = tongGia;
            return this;
        }
        public ViewCartDTO build(){
            return new ViewCartDTO(this) ;
        }
    }
}
