package com.example.webbansach_backend.service.impl;

import com.example.webbansach_backend.Entity.NguoiDung;
import com.example.webbansach_backend.Entity.Sach;
import com.example.webbansach_backend.Entity.SachYeuThich;
import com.example.webbansach_backend.Repository.NguoiDungRepository;
import com.example.webbansach_backend.Repository.SachRepository;
import com.example.webbansach_backend.Repository.SachYeuThichRepository;
import com.example.webbansach_backend.controller.sachController;
import com.example.webbansach_backend.dto.WishLoveDTO;
import com.example.webbansach_backend.service.WishLoveService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class WishLoveServiceImpl implements WishLoveService {
    @Autowired
    private SachRepository sachRepository ;
    @Autowired
    private NguoiDungRepository nguoiDungRepository ;
    @Autowired
    private SachYeuThichRepository sachYeuThichRepository ;


    @Override
    public void addWishLoveList(String tenDangNhap, int maSach) {
        Sach sach = sachRepository.findByMaSach(maSach).orElseThrow() ;
        NguoiDung nguoiDung = nguoiDungRepository.findByTenDangNhap(tenDangNhap).orElseThrow() ;
//        Optional<SachYeuThich> exists = nguoiDung.getDanhSachSachYeuThich().stream().filter(i->i.getSach().getMaSach() == maSach).findFirst() ;
       Optional<SachYeuThich> exists = sachYeuThichRepository.findByNguoiDung_TenDangNhapAndSach_MaSach(tenDangNhap,maSach) ;
        if(exists.isPresent()){
            sachYeuThichRepository.delete(exists.get());
        }else {
            SachYeuThich sachYeuThich1 = new SachYeuThich() ;
            sachYeuThich1.setSach(sach);
            sachYeuThich1.setNguoiDung(nguoiDung);
            sachYeuThich1.setTrangThaiYeuThich(true);
            nguoiDung.getDanhSachSachYeuThich().add(sachYeuThich1) ;
        }

    }
    @Override
    public List<WishLoveDTO> getWishLoveList(String tenDangNhap){
       NguoiDung nguoiDung = nguoiDungRepository.findByTenDangNhap(tenDangNhap).orElseThrow() ;
       List<SachYeuThich> sachYeuThiches = nguoiDung.getDanhSachSachYeuThich() ;
       return sachYeuThiches.stream().map(i->new WishLoveDTO(i.getSach().getTenSach() , i.getSach().getMaSach())).toList() ;
    }
    @Override
    public boolean check(String tenDangNhap,int maSach){
        return sachYeuThichRepository.findByNguoiDung_TenDangNhapAndSach_MaSach(tenDangNhap , maSach).isPresent() ;
    }
}
