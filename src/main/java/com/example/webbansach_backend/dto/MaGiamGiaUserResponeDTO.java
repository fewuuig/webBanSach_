package com.example.webbansach_backend.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
@Data
public class MaGiamGiaUserResponeDTO {
    private int maNguoiDung ;
    List<MaGiamGiaCuaUserResponeDTO> maGiamGiaCuaUserResponeDTOS = new ArrayList<>() ;
}
