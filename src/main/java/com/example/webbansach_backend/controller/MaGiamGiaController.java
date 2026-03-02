package com.example.webbansach_backend.controller;

import com.example.webbansach_backend.Repository.MaGiamGiaRepository;
import com.example.webbansach_backend.dto.UpdateMaGiamGiaDTO;
import com.example.webbansach_backend.service.MaGiamGiaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/vouchers")
public class MaGiamGiaController {
    @Autowired
    private MaGiamGiaService maGiamGiaService ;

    @GetMapping("/user")
    public ResponseEntity<?> getVoucherOfUser(){
        String tenDangNhap = SecurityContextHolder.getContext().getAuthentication().getName() ;
        return ResponseEntity.ok(maGiamGiaService.getMaGiamGiaCuaNguoiDung(tenDangNhap)) ;
    }
//    @PostMapping("/user/use-voucher/{maGiam}")
//    public ResponseEntity<?> useVoucherOfUser(@PathVariable("maGiam") int maGiam){
//        String tenDangNhap = SecurityContextHolder.getContext().getAuthentication().getName() ;
//        maGiamGiaService.dungMaGiamGiaUser(tenDangNhap , maGiam);
//        return ResponseEntity.noContent().build() ;
//    }
    @PutMapping("/update/{maGiam}")
    public ResponseEntity<?> updateVoucher(@PathVariable("maGiam") int maGiam  , @RequestBody UpdateMaGiamGiaDTO updateMaGiamGiaDTO){
        maGiamGiaService.capNhatMaGiamGiaSach(maGiam , updateMaGiamGiaDTO);
        return ResponseEntity.noContent().build() ;
    }
}
