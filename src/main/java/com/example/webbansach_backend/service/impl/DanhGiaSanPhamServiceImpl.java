package com.example.webbansach_backend.service.impl;

import com.example.webbansach_backend.Entity.DanhGia;
import com.example.webbansach_backend.Entity.NguoiDung;
import com.example.webbansach_backend.Entity.Sach;
import com.example.webbansach_backend.Repository.DanhGiaRepository;
import com.example.webbansach_backend.Repository.NguoiDungRepository;
import com.example.webbansach_backend.Repository.SachRepository;
import com.example.webbansach_backend.dto.DanhGiaResponeDTO;
import com.example.webbansach_backend.service.DanhGiaSanPhamService;
import com.example.webbansach_backend.service.NguoiDungService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class DanhGiaSanPhamServiceImpl implements DanhGiaSanPhamService {
    @Autowired
    private NguoiDungRepository nguoiDungRepository ;
    @Autowired
    private SachRepository sachRepository ;
    @Override
    public void addDanhGiaSanPham(String tenDangNhap, String content, int maSach) {
        NguoiDung nguoiDung = nguoiDungRepository.findByTenDangNhap(tenDangNhap).orElseThrow() ;
        Sach sach = sachRepository.findByMaSach(maSach).orElseThrow();

        DanhGia danhGia = new DanhGia() ;
        danhGia.setNhanXet(content);
        danhGia.setNguoiDung(nguoiDung);
        danhGia.setSach(sach);

        nguoiDung.getDanhSachDanhGia().add(danhGia) ;
    }

    @Override
    public List<DanhGiaResponeDTO> getDanhGiaMotQuyenSach(int maSach) {
        Sach sach = sachRepository.findByMaSach(maSach).orElseThrow() ;
        List<DanhGiaResponeDTO> danhGiaResponeDTOS = new ArrayList<>() ;
        for(DanhGia danhGia : sach.getDanhSachDanhGia() ){
            DanhGiaResponeDTO danhGiaResponeDTO = new DanhGiaResponeDTO() ;
            danhGiaResponeDTO.setMaDanhGia(danhGia.getMaDanhGia());
            danhGiaResponeDTO.setNhanXet(danhGia.getNhanXet());
            danhGiaResponeDTO.setDiemXepHang(danhGia.getDiemXepHang());
            danhGiaResponeDTO.setAnhDaiDien(danhGia.getNguoiDung().getAnhDaiDien());

            danhGiaResponeDTOS.add(danhGiaResponeDTO) ;
        }

        return danhGiaResponeDTOS ;
    }
}
