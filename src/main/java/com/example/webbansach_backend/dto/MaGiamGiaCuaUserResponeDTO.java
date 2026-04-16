package com.example.webbansach_backend.dto;

import com.example.webbansach_backend.Enum.DoiTuongApDungMa;
import com.example.webbansach_backend.Enum.LoaiMaGiamGia;
import com.example.webbansach_backend.Enum.TrangThaiMaGiamGia;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
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
    private Double phanTramGiam ;
    private Double tienGiam ;
}
