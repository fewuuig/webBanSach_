package com.example.webbansach_backend.controller;

import com.example.webbansach_backend.Repository.MaGiamGiaRepository;
import com.example.webbansach_backend.dto.MaGiamGiaRequestDTO;
import com.example.webbansach_backend.dto.UpdateMaGiamGiaDTO;
import com.example.webbansach_backend.dto.voucher.UpdateVoucherDTO;
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

    @PutMapping("/update/{maGiam}")
    public ResponseEntity<?> updateVoucher(@PathVariable("maGiam") int maGiam  , @RequestBody UpdateMaGiamGiaDTO updateMaGiamGiaDTO){
        maGiamGiaService.capNhatMaGiamGiaSach(maGiam , updateMaGiamGiaDTO);
        return ResponseEntity.noContent().build() ;
    }
    @PostMapping("/add-voucher")
    public ResponseEntity<?> addVoucher(@RequestBody MaGiamGiaRequestDTO maGiamGiaRequestDTO){
        maGiamGiaService.themMaGiamGia(maGiamGiaRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body("đã thêm") ;
    }
    @GetMapping("/all")
    public ResponseEntity<?> getAllVoucher(){
        return ResponseEntity.ok(maGiamGiaService.getAllVoucher()) ;
    }

    @PutMapping("/update-voucher/{maGiam}")
    public ResponseEntity<?> updateVoucher(@PathVariable("maGiam") int maGiam , @RequestBody UpdateVoucherDTO updateVoucherDTO){
        maGiamGiaService.updateVoucher( updateVoucherDTO,maGiam );
        return ResponseEntity.noContent().build() ;
    }
}
