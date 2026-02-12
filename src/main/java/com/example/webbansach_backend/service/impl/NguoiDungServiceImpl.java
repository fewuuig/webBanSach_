package com.example.webbansach_backend.service.impl;

import com.example.webbansach_backend.Entity.NguoiDung;
import com.example.webbansach_backend.Repository.NguoiDungRepository;
import com.example.webbansach_backend.dto.NguoiDungRequestDTO;
import com.example.webbansach_backend.service.NguoiDungService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NguoiDungServiceImpl implements NguoiDungService {
    @Autowired
    private NguoiDungRepository nguoiDungRepository ;
    @Autowired
    private ModelMapper modelMapper ;
    @Override
    public NguoiDungRequestDTO getThongTin(String tenDangNhap){
        NguoiDung nguoiDung =  nguoiDungRepository.findByTenDangNhap(tenDangNhap).orElseThrow() ;
        return modelMapper.map(nguoiDung , NguoiDungRequestDTO.class) ;
    }
}
