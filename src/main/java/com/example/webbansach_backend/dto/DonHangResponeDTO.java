package com.example.webbansach_backend.dto;

import com.example.webbansach_backend.Enum.TrangThaiGiaoHang;
import lombok.Data;

@Data
public class DonHangResponeDTO {
    private int maDonHang ;
    private double chiPhiGiaoHang ;
    private String diaChiNhanHang ;
    private TrangThaiGiaoHang trangThai ;
}
