package com.example.webbansach_backend.dto;

import com.example.webbansach_backend.Enum.LoaiMaGiamGia;
import com.example.webbansach_backend.Enum.TrangThaiMaGiamGia;
import lombok.Data;

import java.time.LocalDateTime;
@Data
public class MaGiamGiaCuaUserResponeDTO {
    private int maGiamNguoiDung ;
    private int maGiam ;
    private int daDung ;
    private String tenMaGiamGia ;
    private int luotDungToiDa ;
    private int soMaDaDung ;
    private int soLuong ;
    private LocalDateTime ngayHetHan ;
    private LocalDateTime ngayBatDau ;
    private LoaiMaGiamGia loaiMaGiamGia ;
    private double giamToiDa ;
    private double donGiaTu ;
    private TrangThaiMaGiamGia trangThaiMaGiamGia ;
}
