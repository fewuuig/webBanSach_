package com.example.webbansach_backend.controller;

import com.example.webbansach_backend.service.HinhThucThanhToanService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/thanh-toan")
public class HinhThucThanhToanController {

    private static final Logger log = LoggerFactory.getLogger(HinhThucThanhToanController.class);

    // 🔥 PERF LOGGER
    private static final Logger perfLogger = LoggerFactory.getLogger("PERF_LOGGER");

    @Autowired
    private HinhThucThanhToanService hinhThucThanhToanService;

    // ================= GET ALL =================
    @GetMapping("/lay-hinh-thuc-thanh-toan")
    public ResponseEntity<?> getHinhThucThanhToan(){

        long start = System.currentTimeMillis();

        try {
            Object result = hinhThucThanhToanService.getHinhThucThanhToans();

            long time = System.currentTimeMillis() - start;

            perfLogger.info("GET /thanh-toan/lay-hinh-thuc-thanh-toan | time={} ms",
                    time);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            long time = System.currentTimeMillis() - start;

            log.error("ERROR GET /thanh-toan/lay-hinh-thuc-thanh-toan | time={} ms | error={}",
                    time, e.getMessage());

            throw e;
        }
    }
}