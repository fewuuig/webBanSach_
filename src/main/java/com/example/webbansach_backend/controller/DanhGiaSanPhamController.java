package com.example.webbansach_backend.controller;

import com.example.webbansach_backend.service.DanhGiaSanPhamService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/danh-gia")
public class DanhGiaSanPhamController {

    private static final Logger log = LoggerFactory.getLogger(DanhGiaSanPhamController.class);

    // 🔥 PERF LOGGER
    private static final Logger perfLogger = LoggerFactory.getLogger("PERF_LOGGER");

    @Autowired
    private DanhGiaSanPhamService danhGiaSanPhamService;

    // ================= ADD =================
    @PostMapping("/{maSach}/{noiDungDanhGia}")
    public ResponseEntity<?> addDanhGiaSanPham(
            @PathVariable("maSach") int maSach,
            @PathVariable("noiDungDanhGia") String nhanXet) {

        long start = System.currentTimeMillis();

        try {
            String tenDangNhap = SecurityContextHolder.getContext().getAuthentication().getName();

            danhGiaSanPhamService.addDanhGiaSanPham(tenDangNhap, nhanXet, maSach);

            long time = System.currentTimeMillis() - start;

            perfLogger.info("POST /danh-gia/{maSach}/{noiDungDanhGia} | user={} | bookId={} | time={} ms",
                    tenDangNhap, maSach, time);

            return ResponseEntity.ok("Đã thêm nhận xét");

        } catch (Exception e) {
            long time = System.currentTimeMillis() - start;

            log.error("ERROR POST /danh-gia | time={} ms | error={}",
                    time, e.getMessage());

            throw e;
        }
    }

    // ================= GET =================
    @GetMapping("/{maSach}/danhSachDanhGia")
    public ResponseEntity<?> getDanhGiaMotQuyenSach(@PathVariable("maSach") int maSach) {

        long start = System.currentTimeMillis();

        try {
            Object result = danhGiaSanPhamService.getDanhGiaMotQuyenSachCache(maSach);

            long time = System.currentTimeMillis() - start;

            perfLogger.info("GET /danh-gia/{maSach}/danhSachDanhGia | bookId={} | time={} ms",
                    maSach, time);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            long time = System.currentTimeMillis() - start;

            log.error("ERROR GET /danh-gia/{maSach}/danhSachDanhGia | time={} ms | error={}",
                    time, e.getMessage());

            throw e;
        }
    }
}