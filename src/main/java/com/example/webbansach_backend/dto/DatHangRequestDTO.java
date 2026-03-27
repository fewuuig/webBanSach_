package com.example.webbansach_backend.dto;

import com.example.webbansach_backend.dto.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DatHangRequestDTO {
    List<OrderItem> items ;
    private Integer maGiam ;
    private Set<Long> danhSachSanPhamChon ;
    private int maDiaChiGiaoHang;
    private int maHinhThucThanhToan ;
    private int maHinhThucGiaoHang ;
}