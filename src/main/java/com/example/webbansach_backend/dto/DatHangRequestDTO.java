package com.example.webbansach_backend.dto;

import com.example.webbansach_backend.dto.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DatHangRequestDTO {
    List<OrderItem> items ;
    private Integer maGiam ;
    private int maDiaChiGiaoHang;
    private int maHinhThucThanhToan ;
    private int maHinhThucGiaoHang ;
}