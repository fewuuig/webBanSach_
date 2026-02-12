package com.example.webbansach_backend.controller;

import com.example.webbansach_backend.dto.DiaChiGiaoHangRequestDTO;
import com.example.webbansach_backend.service.DiaChiGiaoHangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dia-chi")
public class DiaChiGiaoHangController {
    @Autowired
    private DiaChiGiaoHangService diaChiGiaoHangService ;
    @GetMapping("/dia-chi-giao-hang")
    public ResponseEntity<?> getDiaChiGiaoHang(){
        String tenDangNhap = SecurityContextHolder.getContext().getAuthentication().getName() ;
        return ResponseEntity.ok(diaChiGiaoHangService.getDiaChiGiaoHang(tenDangNhap)) ;
    }
    @PostMapping ("/them-dia-chi-giao-hang")
    public ResponseEntity<?> addDiaChiGiaoHang(@RequestBody DiaChiGiaoHangRequestDTO diaChiGiaoHangRequestDTO){
        String tenDangNhap = SecurityContextHolder.getContext().getAuthentication().getName() ;
        diaChiGiaoHangService.addDiaChiGiaoHang(tenDangNhap , diaChiGiaoHangRequestDTO) ;
        System.out.println("Thêm địa chỉ thành công");
        return ResponseEntity.ok("Thêm địa chỉ giao hnagf thannhf công") ;
    }
}
