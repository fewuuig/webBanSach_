package com.example.webbansach_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HinhThucThanhToanResponeDTO {
    private int maHinhThucThanhToan ;
    private String tenHinhThucThanhToan ;
    private String moTa ;
}
