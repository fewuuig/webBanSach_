package com.example.webbansach_backend.service.impl;

import com.example.webbansach_backend.Entity.HinhThucThanhToan;
import com.example.webbansach_backend.Repository.HinhThucThanhToanRepository;
import com.example.webbansach_backend.dto.HinhThucThanhToanResponeDTO;
import com.example.webbansach_backend.service.HinhThucThanhToanService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Transactional
@Service
public class HinhThucThanhToanServiceImpl implements HinhThucThanhToanService {
    @Autowired
    private HinhThucThanhToanRepository hinhThucThanhToanRepository ;
    @Override
    public List<HinhThucThanhToanResponeDTO> getHinhThucThanhToans() {
        List<HinhThucThanhToanResponeDTO> hinhThucThanhToanResponeDTOS= new ArrayList<>() ;
        List<HinhThucThanhToan> hinhThucThanhToans = hinhThucThanhToanRepository.findAll() ;
        for(HinhThucThanhToan hinhThucThanhToan : hinhThucThanhToans){
            HinhThucThanhToanResponeDTO hinhThucThanhToanResponeDTO = new HinhThucThanhToanResponeDTO() ;
            hinhThucThanhToanResponeDTO.setMaHinhThucThanhToan(hinhThucThanhToan.getMaHinhThucThanhToan());
            hinhThucThanhToanResponeDTO.setTenHinhThucThanhToan(hinhThucThanhToan.getTenHinhThucThanhToan());
            hinhThucThanhToanResponeDTO.setMoTa(hinhThucThanhToan.getMoTa());
            hinhThucThanhToanResponeDTOS.add(hinhThucThanhToanResponeDTO) ;
        }
        return hinhThucThanhToanResponeDTOS;
    }
}
