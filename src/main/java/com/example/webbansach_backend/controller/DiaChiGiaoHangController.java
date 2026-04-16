package com.example.webbansach_backend.controller;

import com.example.webbansach_backend.dto.DiaChiGiaoHangRequestDTO;
import com.example.webbansach_backend.service.DiaChiGiaoHangService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/dia-chi")
public class DiaChiGiaoHangController {

    private static final Logger log = LoggerFactory.getLogger(DiaChiGiaoHangController.class);

    // 🔥 PERF LOGGER
    private static final Logger perfLogger = LoggerFactory.getLogger("PERF_LOGGER");

    @Autowired
    private DiaChiGiaoHangService diaChiGiaoHangService;

    // ================= GET =================
    @GetMapping("/dia-chi-giao-hang")
    public ResponseEntity<?> getDiaChiGiaoHang(){

        long start = System.currentTimeMillis();

        try {
            String tenDangNhap = SecurityContextHolder.getContext().getAuthentication().getName();

            Object result = diaChiGiaoHangService.getDiaChiGiaoHang(tenDangNhap);

            long time = System.currentTimeMillis() - start;

            perfLogger.info("GET /dia-chi/dia-chi-giao-hang | user={} | time={} ms",
                    tenDangNhap, time);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            long time = System.currentTimeMillis() - start;

            log.error("ERROR GET /dia-chi/dia-chi-giao-hang | time={} ms | error={}",
                    time, e.getMessage());

            throw e;
        }
    }

    // ================= ADD =================
    @PostMapping("/them-dia-chi-giao-hang")
    public ResponseEntity<?> addDiaChiGiaoHang(@RequestBody DiaChiGiaoHangRequestDTO diaChiGiaoHangRequestDTO){

        long start = System.currentTimeMillis();

        try {
            String tenDangNhap = SecurityContextHolder.getContext().getAuthentication().getName();

            diaChiGiaoHangService.addDiaChiGiaoHang(tenDangNhap, diaChiGiaoHangRequestDTO);

            long time = System.currentTimeMillis() - start;

            perfLogger.info("POST /dia-chi/them-dia-chi-giao-hang | user={} | time={} ms",
                    tenDangNhap, time);

            return ResponseEntity.ok("Thêm địa chỉ giao hàng thành công");

        } catch (Exception e) {
            long time = System.currentTimeMillis() - start;

            log.error("ERROR POST /dia-chi/them-dia-chi-giao-hang | time={} ms | error={}",
                    time, e.getMessage());

            throw e;
        }
    }
}