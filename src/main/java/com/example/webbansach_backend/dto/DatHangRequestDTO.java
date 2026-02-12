package com.example.webbansach_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DatHangRequestDTO {
    private int maSach ;
    private int soLuong ;
    private int maDiaChiGiaoHang;
    private int maHinhThucThanhToan ;
    private int maHinhThucGiaoHang ;
}
