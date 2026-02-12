package com.example.webbansach_backend.dto;

import com.example.webbansach_backend.Enum.TrangThaiGiaoHang;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class DonHangResponeDTO {
    private int maDonHang ;
    private double chiPhiGiaoHang ;
    private String diaChiNhanHang ;
    private TrangThaiGiaoHang trangThai ;
}
