package com.example.webbansach_backend.controller;

import com.example.webbansach_backend.Repository.HinhThucThanhToanRepository;
import com.example.webbansach_backend.service.HinhThucThanhToanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/thanh-toan")
public class HinhThucThanhToanController {
    @Autowired
    private HinhThucThanhToanService hinhThucThanhToanService ;

    @GetMapping("/lay-hinh-thuc-thanh-toan")
    public ResponseEntity getHinhThucThanhToan(){
        return ResponseEntity.ok(hinhThucThanhToanService.getHinhThucThanhToans()) ;
    }

}
