package com.example.webbansach_backend.dto.book;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookResponeV2DTO {
    private int ma_sach ;
    private String ten_sach ;
    private String ten_tac_gia ;

    private String ma_ta ;
    private double gia_niem_yet ;
    private double gia_ban ;
    private int so_luong ;
    private double trung_binh_xep_hang ;

}
