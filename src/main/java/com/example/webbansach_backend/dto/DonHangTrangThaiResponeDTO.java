package com.example.webbansach_backend.dto;

import com.example.webbansach_backend.Enum.TrangThaiGiaoHang;
import lombok.Data;

import java.util.List;

@Data
public class DonHangTrangThaiResponeDTO {
    private int maDonHang ;
    private double chiPhiGiaoHang ;
    private String diaChiNhanHang ;
    private TrangThaiGiaoHang trangThai ;
    List<SachTrongDonDTO> sachTrongDonDTOS ;
}
