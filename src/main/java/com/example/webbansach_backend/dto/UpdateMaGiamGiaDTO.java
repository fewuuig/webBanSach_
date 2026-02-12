package com.example.webbansach_backend.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UpdateMaGiamGiaDTO {
    private LocalDateTime ngayHetHan ;
    private Integer soLuong ;
    private Double giamToiDa ;
    private Double donGiaTu ;
}
