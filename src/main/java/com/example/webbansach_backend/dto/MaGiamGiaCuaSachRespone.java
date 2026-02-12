package com.example.webbansach_backend.dto;

import com.example.webbansach_backend.Enum.DoiTuongApDungMa;
import com.example.webbansach_backend.Enum.LoaiMaGiamGia;
import com.example.webbansach_backend.Enum.TrangThaiMaGiamGia;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
public class MaGiamGiaCuaSachRespone {
    private int maGiam ;
    private String tenMaGiamGia ;
    private int soMaDaDung ;
    private int soLuong ;
    private LocalDateTime ngayHetHan ;
    private LocalDateTime ngayBatDau ;
    private LoaiMaGiamGia loaiMaGiamGia ;
    private double giamToiDa ;
    private double donGiaTu ;
    private DoiTuongApDungMa doiTuongApDungMa ;
    private TrangThaiMaGiamGia trangThaiMaGiamGia ;

}
