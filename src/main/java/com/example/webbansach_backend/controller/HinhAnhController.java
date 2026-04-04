package com.example.webbansach_backend.controller;

import com.example.webbansach_backend.service.HinhAnhService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/image")
public class HinhAnhController {

    private static final Logger log = LoggerFactory.getLogger(HinhAnhController.class);

    // 🔥 PERF LOGGER riêng
    private static final Logger perfLogger = LoggerFactory.getLogger("PERF_LOGGER");

    @Autowired
    private HinhAnhService hinhAnhService;

    @GetMapping("/book/{maSach}/{soLuong}")
    public ResponseEntity<?> getAnh(@PathVariable("maSach") int maSach ,
                                    @PathVariable("soLuong") int soLuong){
        return ResponseEntity.ok(hinhAnhService.getAnhCuaMotSach(maSach ,soLuong)) ;

    }
}