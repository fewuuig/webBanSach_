package com.example.webbansach_backend.dto;

import com.example.webbansach_backend.Enum.DoiTuongApDungMa;
import com.example.webbansach_backend.Enum.LoaiMaGiamGia;
import com.example.webbansach_backend.Enum.TrangThaiMaGiamGia;
import lombok.Data;

import java.time.LocalDateTime;
@Data
public class MaGiamGiaCuaUserResponeDTO {
    private int maGiamNguoiDung ;//maGIamNGuoiDUng
    private int maGiam ;
    private int daDung ; // maGIamNGuoiDUng
    private String tenMaGiamGia ;
    private int soMaDaDung ;
    private int soLuong ;
    private LocalDateTime ngayHetHan ;
    private LocalDateTime ngayBatDau ;
    private LoaiMaGiamGia loaiMaGiamGia ;
    private double giamToiDa ;
    private double donGiaTu ;
    private TrangThaiMaGiamGia trangThaiMaGiamGia ;
    private DoiTuongApDungMa doiTuongApDungMa ;

}
