package com.example.webbansach_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DiaChiGiaoHangRequestDTO {
    private String tinhOrCity ;
    private String quanOrHuyen ;
    private String phuongOrXa ;
    private String soNha ;

}
