package com.example.webbansach_backend.controller;

import com.example.webbansach_backend.service.HinhThucGiaoHangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/giao-hang")
public class HinhThucGiaoHangController {
    @Autowired
    private HinhThucGiaoHangService hinhThucGiaoHangService ;
    @GetMapping("/hinh-thuc-giao-hang")
    public ResponseEntity getHinhThucGiaoHang(){
        return ResponseEntity.ok(hinhThucGiaoHangService.getHinhThucGiaoHang()) ;
    }
}
