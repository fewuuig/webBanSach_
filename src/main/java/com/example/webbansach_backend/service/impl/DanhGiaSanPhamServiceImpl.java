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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class DanhGiaSanPhamServiceImpl implements DanhGiaSanPhamService {
    @Autowired
    private NguoiDungRepository nguoiDungRepository ;
    @Autowired
    private SachRepository sachRepository ;
    @Autowired
    private RedisTemplate<String ,DanhGiaResponeDTO> redisDanhGia ;
    @Override
    @Transactional
    public void addDanhGiaSanPham(String tenDangNhap, String content, int maSach) {
        NguoiDung nguoiDung = nguoiDungRepository.findByTenDangNhap(tenDangNhap).orElseThrow() ;
        Sach sach = sachRepository.findByMaSachAndIsActive(maSach,true).orElseThrow();

        DanhGia danhGia = new DanhGia() ;
        danhGia.setNhanXet(content);
        danhGia.setNguoiDung(nguoiDung);
        danhGia.setSach(sach);

        nguoiDung.getDanhSachDanhGia().add(danhGia) ;
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                DanhGiaResponeDTO danhGiaResponeDTO = new DanhGiaResponeDTO() ;
                danhGiaResponeDTO.setMaDanhGia(danhGia.getMaDanhGia());
                danhGiaResponeDTO.setNhanXet(danhGia.getNhanXet());
                danhGiaResponeDTO.setDiemXepHang(danhGia.getDiemXepHang());
                danhGiaResponeDTO.setAnhDaiDien(nguoiDung.getAnhDaiDien());
                redisDanhGia.opsForList().rightPush("book:"+maSach+":comment" , danhGiaResponeDTO) ;
            }
        });
    }

    // suwr lys tahwts coor chai taij ddaay
    // cho nos vaof 1 key lisst danh sach danh gia trong ram
    @Override
    public List<DanhGiaResponeDTO> getDanhGiaMotQuyenSach(int maSach) {
        Sach sach = sachRepository.findByMaSachAndIsActiveFetchDanhGia(maSach,true).orElseThrow() ;
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

    @Override
    public List<DanhGiaResponeDTO> getDanhGiaMotQuyenSachCache(int maSach){
        // thieeys kees key
        List<DanhGiaResponeDTO> cached = redisDanhGia.opsForList().range("book:"+maSach+":comment" , 0 , -1) ;
        if(cached !=null &&  !cached.isEmpty()){
            return cached ;
        }

        Sach sach = sachRepository.findByMaSachAndIsActiveFetchDanhGia(maSach,true).
                orElseThrow(()-> new RuntimeException("sách không còn hoạt động")) ;
        List<DanhGiaResponeDTO> danhGiaResponeDTOS1 = new ArrayList<>() ;
        if(sach.getDanhSachDanhGia() == null || sach.getDanhSachDanhGia() .isEmpty()) return null;
        for(DanhGia danhGia : sach.getDanhSachDanhGia() ){
            DanhGiaResponeDTO danhGiaResponeDTO = new DanhGiaResponeDTO() ;
            danhGiaResponeDTO.setMaDanhGia(danhGia.getMaDanhGia());
            danhGiaResponeDTO.setNhanXet(danhGia.getNhanXet());
            danhGiaResponeDTO.setDiemXepHang(danhGia.getDiemXepHang());
            danhGiaResponeDTO.setAnhDaiDien(danhGia.getNguoiDung().getAnhDaiDien());
            danhGiaResponeDTOS1.add(danhGiaResponeDTO) ;

        }
        redisDanhGia.opsForList().rightPushAll("book:"+maSach+":comment" ,danhGiaResponeDTOS1 ) ;
        redisDanhGia.expire("book:"+maSach+":comment", Duration.ofHours(1)) ;
        return danhGiaResponeDTOS1 ;
    }
}
