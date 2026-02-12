package com.example.webbansach_backend.controller;

import com.example.webbansach_backend.service.DanhGiaSanPhamService;
import jakarta.persistence.Entity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/danh-gia")
public class DanhGiaSanPhamController {
    @Autowired
    private DanhGiaSanPhamService danhGiaSanPhamService ;

    @PostMapping("/{maSach}/{noiDungDanhGia}")
    public ResponseEntity<?> addDanhGiaSanPham(@PathVariable(value = "maSach" ) int maSach, @PathVariable(value = "noiDungDanhGia")String nhanXet ){
        String tenDangNhap = SecurityContextHolder.getContext().getAuthentication().getName() ;
        danhGiaSanPhamService.addDanhGiaSanPham(tenDangNhap , nhanXet , maSach);
        System.out.println("đã thêm nhận xét của sách mã : "+maSach);
        return ResponseEntity.ok(HttpStatus.CREATED) ;
    }
    @GetMapping("/{maSach}/danhSachDanhGia")
    public ResponseEntity<?> getDanhGiaMotQuyenSach(@PathVariable(value = "maSach") int maSach ){
        return ResponseEntity.ok(danhGiaSanPhamService.getDanhGiaMotQuyenSach(maSach)) ;
    }
}
