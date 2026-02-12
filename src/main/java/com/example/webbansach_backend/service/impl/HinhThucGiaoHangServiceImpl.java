package com.example.webbansach_backend.service.impl;

import com.example.webbansach_backend.Entity.HinhThucGiaoHang;
import com.example.webbansach_backend.Entity.HinhThucThanhToan;
import com.example.webbansach_backend.Repository.HinhThucGiaoHangRepository;
import com.example.webbansach_backend.Repository.HinhThucThanhToanRepository;
import com.example.webbansach_backend.dto.HinhThucGiaoHangResponeDTO;
import com.example.webbansach_backend.dto.HinhThucThanhToanResponeDTO;
import com.example.webbansach_backend.service.HinhThucGiaoHangService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Service
@Transactional
public class HinhThucGiaoHangServiceImpl implements HinhThucGiaoHangService {
    @Autowired
    private HinhThucGiaoHangRepository hinhThucGiaoHangRepository ;
    @Override
    public List<HinhThucGiaoHangResponeDTO> getHinhThucGiaoHang() {
        List<HinhThucGiaoHangResponeDTO> hinhThucThanhToanResponeDTOS = new ArrayList<>() ;
        List<HinhThucGiaoHang> hinhThucGiaoHangs = hinhThucGiaoHangRepository.findAll() ;
        for(HinhThucGiaoHang hinhThucGiaoHang : hinhThucGiaoHangs){
            HinhThucGiaoHangResponeDTO hinhThucGiaoHangResponeDTO = new HinhThucGiaoHangResponeDTO() ;
            hinhThucGiaoHangResponeDTO.setMaHinhThucGiaoHang(hinhThucGiaoHang.getMaHinhThucGiaoHang());
            hinhThucGiaoHangResponeDTO.setTenHinhThucGiaoHang(hinhThucGiaoHang.getTenHinhThucGiaoHang());
            hinhThucGiaoHangResponeDTO.setMoTa(hinhThucGiaoHang.getMoTa());
            hinhThucThanhToanResponeDTOS.add(hinhThucGiaoHangResponeDTO) ;
        }
        return hinhThucThanhToanResponeDTOS ;
    }
}
