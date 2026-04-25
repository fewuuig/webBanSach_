package com.example.webbansach_backend.dto;

import com.example.webbansach_backend.Enum.TrangThaiGiaoHang;
import lombok.Data;

import java.util.List;

@Data
public class DonHangTrangThaiResponeDTO {
    private String soDienThoai ;
    private String hoTen ;
    private Long maDonHang ;
    private double chiPhiGiaoHang ;
    private String diaChiNhanHang ;
    private TrangThaiGiaoHang trangThai ;
    private double tongGia ;
    List<SachTrongDonDTO> sachTrongDonDTOS ;
}
