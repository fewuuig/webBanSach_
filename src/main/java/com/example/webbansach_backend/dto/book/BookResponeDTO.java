package com.example.webbansach_backend.dto.book;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor

public class BookResponeDTO {
    private int maSach ;
    private String tenSach ;
    private String tenTacGia ;
    private String isbn ;
    private String moTa ;
    private double giaNiemYet ;
    private double giaBan ;
    private int soLuong ;
    private double trungBinhXepHang ;

    public BookResponeDTO(int ma_sach,
                          String ten_sach,
                          String ten_tac_gia,
                          String mo_ta,
                          double gia_niem_yet,
                          double gia_ban,
                          int so_luong,
                          double trung_binh_xep_hang) {
        this.maSach = ma_sach;
        this.tenSach = ten_sach;
        this.tenTacGia = ten_tac_gia;
        this.moTa = mo_ta;
        this.giaNiemYet = gia_niem_yet;
        this.giaBan = gia_ban;
        this.soLuong = so_luong;
        this.trungBinhXepHang = trung_binh_xep_hang;
    }
}
