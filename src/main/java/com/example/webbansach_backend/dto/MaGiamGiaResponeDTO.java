package com.example.webbansach_backend.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class MaGiamGiaResponeDTO {
    private int maSach;

    private List<MaGiamGiaCuaSachRespone> maGiamGiaCuaSachRespones = new ArrayList<>();

}