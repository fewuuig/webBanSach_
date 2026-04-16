package com.example.webbansach_backend.controller;

import com.example.webbansach_backend.service.HinhThucGiaoHangService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/giao-hang")
public class HinhThucGiaoHangController {

    private static final Logger log = LoggerFactory.getLogger(HinhThucGiaoHangController.class);

    // 🔥 PERF LOGGER
    private static final Logger perfLogger = LoggerFactory.getLogger("PERF_LOGGER");

    @Autowired
    private HinhThucGiaoHangService hinhThucGiaoHangService;

    // ================= GET ALL =================
    @GetMapping("/hinh-thuc-giao-hang")
    public ResponseEntity<?> getHinhThucGiaoHang(){

        long start = System.currentTimeMillis();

        try {
            Object result = hinhThucGiaoHangService.getHinhThucGiaoHang();

            long time = System.currentTimeMillis() - start;

            perfLogger.info("GET /giao-hang/hinh-thuc-giao-hang | time={} ms",
                    time);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            long time = System.currentTimeMillis() - start;

            log.error("ERROR GET /giao-hang/hinh-thuc-giao-hang | time={} ms | error={}",
                    time, e.getMessage());

            throw e;
        }
    }
}