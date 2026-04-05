package com.example.webbansach_backend.dto;

import com.example.webbansach_backend.Enum.DoiTuongApDungMa;
import com.example.webbansach_backend.Enum.LoaiMaGiamGia;
import com.example.webbansach_backend.Enum.TrangThaiMaGiamGia;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class MaGiamGiaRequestDTO {
    private String tenMaGiamGia ;
    private LocalDateTime ngayBatDau ;
    private LocalDateTime ngayHetHan ;
    private int soLuong ;
    private int soMaDaDung ;
    private Double giamToiDa ;
    private Double donGiaTu ;
    private DoiTuongApDungMa doiTuongApDungMa ;
    private Double phanTramGiam ;
    private Double tienGiam ;
    private TrangThaiMaGiamGia trangThaiMaGiamGia ;
    private int gioiHanSoLuongDungUser ;
    private LoaiMaGiamGia loaiMaGiamGia ;
}
