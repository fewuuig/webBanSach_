package com.example.webbansach_backend.dto;

import lombok.Data;

import java.util.Set;
@Data
public class DatHangFromCartRequestDTO {
    private Set<Long> danhSachSanPhamDatHang ;
    private int maDiaChiGiaoHang;
    private int maHinhThucThanhToan ;
    private int maHinhThucGiaoHang ;

}
