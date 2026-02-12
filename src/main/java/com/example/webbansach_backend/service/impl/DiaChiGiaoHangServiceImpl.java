package com.example.webbansach_backend.service.impl;

import com.example.webbansach_backend.Entity.DiaChiGiaoHang;
import com.example.webbansach_backend.Entity.DonHang;
import com.example.webbansach_backend.Entity.NguoiDung;
import com.example.webbansach_backend.Repository.DonHangRepository;
import com.example.webbansach_backend.Repository.NguoiDungRepository;
import com.example.webbansach_backend.converter.DiaChiGiaoHangResponeDTOConverter;
import com.example.webbansach_backend.dto.DiaChiGiaoHangRequestDTO;
import com.example.webbansach_backend.dto.DiaChiGiaoHangResponeDTO;
import com.example.webbansach_backend.service.DiaChiGiaoHangService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
@Transactional
@Service
public class DiaChiGiaoHangServiceImpl implements DiaChiGiaoHangService {
    @Autowired
    private NguoiDungRepository nguoiDungRepository ;
    @Autowired
    private DonHangRepository donHangRepository ;

    @Override
    public List<DiaChiGiaoHangResponeDTO> getDiaChiGiaoHang(String tenDangNhap) {
        NguoiDung nguoiDung = nguoiDungRepository.findByTenDangNhap(tenDangNhap).orElseThrow() ;
        List<DiaChiGiaoHang> diaChiGiaoHangs = nguoiDung.getDiaChiGiaoHangs() ;
        return diaChiGiaoHangs.stream().map(DiaChiGiaoHangResponeDTOConverter::toDiaChiGiaoHangResponeDTO).toList() ;
    }

    @Override
    public void addDiaChiGiaoHang(String tenDangNhap , DiaChiGiaoHangRequestDTO diaChiGiaoHangRequestDTO) {
       NguoiDung nguoiDung = nguoiDungRepository.findByTenDangNhap(tenDangNhap).orElseThrow() ;
       DiaChiGiaoHang diaChiGiaoHang = new DiaChiGiaoHang() ;
       diaChiGiaoHang.setTinhOrCity(diaChiGiaoHangRequestDTO.getTinhOrCity());
       diaChiGiaoHang.setQuanOrHuyen(diaChiGiaoHangRequestDTO.getQuanOrHuyen());
       diaChiGiaoHang.setNguoiDung(nguoiDung);
       diaChiGiaoHang.setPhuongOrXa(diaChiGiaoHangRequestDTO.getPhuongOrXa());
       diaChiGiaoHang.setSoNha(diaChiGiaoHangRequestDTO.getSoNha());
       nguoiDung.getDiaChiGiaoHangs().add(diaChiGiaoHang) ;
    }
}
