package com.example.webbansach_backend.controller;

import com.example.webbansach_backend.dto.MaGiamGiaRequestDTO;
import com.example.webbansach_backend.dto.UpdateMaGiamGiaDTO;
import com.example.webbansach_backend.dto.voucher.UpdateVoucherDTO;
import com.example.webbansach_backend.service.MaGiamGiaService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/vouchers")
public class MaGiamGiaController {

    private static final Logger log = LoggerFactory.getLogger(MaGiamGiaController.class);

    // 🔥 PERF LOGGER giống BookController
    private static final Logger perfLogger = LoggerFactory.getLogger("PERF_LOGGER");

    @Autowired
    private MaGiamGiaService maGiamGiaService;

    // ================= USER VOUCHER =================
    @GetMapping("/user")
    public ResponseEntity<?> getVoucherOfUser(){

        long start = System.currentTimeMillis();

        try {
            String tenDangNhap = SecurityContextHolder.getContext().getAuthentication().getName();

            Object result = maGiamGiaService.getMaGiamGiaCuaNguoiDung(tenDangNhap);

            long time = System.currentTimeMillis() - start;

            perfLogger.info("GET /vouchers/user | user={} | time={} ms",
                    tenDangNhap, time);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            long time = System.currentTimeMillis() - start;

            log.error("ERROR GET /vouchers/user | time={} ms | error={}",
                    time, e.getMessage());

            throw e;
        }
    }

    // ================= UPDATE =================
    @PutMapping("/update/{maGiam}")
    public ResponseEntity<?> updateVoucher(@PathVariable("maGiam") int maGiam,
                                           @RequestBody UpdateMaGiamGiaDTO updateMaGiamGiaDTO){

        long start = System.currentTimeMillis();

        try {
            maGiamGiaService.capNhatMaGiamGiaSach(maGiam, updateMaGiamGiaDTO);

            long time = System.currentTimeMillis() - start;

            perfLogger.info("PUT /vouchers/update | id={} | time={} ms",
                    maGiam, time);

            return ResponseEntity.noContent().build();

        } catch (Exception e) {
            long time = System.currentTimeMillis() - start;

            log.error("ERROR PUT /vouchers/update | time={} ms | error={}",
                    time, e.getMessage());

            throw e;
        }
    }

    // ================= ADD =================
    @PostMapping("/add-voucher")
    public ResponseEntity<?> addVoucher(@RequestBody MaGiamGiaRequestDTO maGiamGiaRequestDTO){

        long start = System.currentTimeMillis();

        try {
            maGiamGiaService.themMaGiamGia(maGiamGiaRequestDTO);

            long time = System.currentTimeMillis() - start;

            perfLogger.info("POST /vouchers/add-voucher | time={} ms",
                    time);

            return ResponseEntity.status(HttpStatus.CREATED).body("đã thêm");

        } catch (Exception e) {
            long time = System.currentTimeMillis() - start;

            log.error("ERROR POST /vouchers/add-voucher | time={} ms | error={}",
                    time, e.getMessage());

            throw e;
        }
    }

    // ================= GET ALL =================
    @GetMapping("/all")
    public ResponseEntity<?> getAllVoucher(){

        long start = System.currentTimeMillis();

        try {
            Object result = maGiamGiaService.getAllVoucher();

            long time = System.currentTimeMillis() - start;

            perfLogger.info("GET /vouchers/all | time={} ms",
                    time);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            long time = System.currentTimeMillis() - start;

            log.error("ERROR GET /vouchers/all | time={} ms | error={}",
                    time, e.getMessage());

            throw e;
        }
    }

    // ================= UPDATE VOUCHER =================
    @PutMapping("/update-voucher/{maGiam}")
    public ResponseEntity<?> updateVoucher2(@PathVariable("maGiam") int maGiam,
                                            @RequestBody UpdateVoucherDTO updateVoucherDTO){

        long start = System.currentTimeMillis();

        try {
            maGiamGiaService.updateVoucher(updateVoucherDTO, maGiam);

            long time = System.currentTimeMillis() - start;

            perfLogger.info("PUT /vouchers/update-voucher | id={} | time={} ms",
                    maGiam, time);

            return ResponseEntity.noContent().build();

        } catch (Exception e) {
            long time = System.currentTimeMillis() - start;

            log.error("ERROR PUT /vouchers/update-voucher | time={} ms | error={}",
                    time, e.getMessage());

            throw e;
        }
    }
}