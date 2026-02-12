package com.example.webbansach_backend.converter;

import com.example.webbansach_backend.Entity.DiaChiGiaoHang;
import com.example.webbansach_backend.dto.DiaChiGiaoHangResponeDTO;

public class DiaChiGiaoHangResponeDTOConverter {

    public static DiaChiGiaoHangResponeDTO toDiaChiGiaoHangResponeDTO(DiaChiGiaoHang diaChiGiaoHang){
        DiaChiGiaoHangResponeDTO diaChiGiaoHangResponeDTO = new DiaChiGiaoHangResponeDTO.Builder()
                .setMaDiaChi(diaChiGiaoHang.getMaDiaChiGiaoHang())
                .setTinhOrCity(diaChiGiaoHang.getTinhOrCity())
                .setQuanOrHuyen(diaChiGiaoHang.getQuanOrHuyen())
                .setPhuongOrXa(diaChiGiaoHang.getPhuongOrXa())
                .setSoNha(diaChiGiaoHang.getSoNha()).build() ;
        return diaChiGiaoHangResponeDTO ;
    }
}
