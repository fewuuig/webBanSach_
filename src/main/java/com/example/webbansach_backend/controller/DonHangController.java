package com.example.webbansach_backend.controller;

import com.example.webbansach_backend.Enum.TrangThaiGiaoHang;
import com.example.webbansach_backend.service.impl.OrderServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// cập nhật trạng thái của đơn hàng
@RestController
@RequestMapping("/don-hang")
public class DonHangController {

    private static final Logger log = LoggerFactory.getLogger(DonHangController.class);

    // 🔥 PERF LOGGER
    private static final Logger perfLogger = LoggerFactory.getLogger("PERF_LOGGER");

    @Autowired
    private OrderServiceImpl orderService;

    // ================= UPDATE STATUS =================
    @PutMapping("/{id}/trang-thai")
    public ResponseEntity<?> capNhatTrangThaiGiaoHang(@PathVariable int id,
                                                      @RequestParam("trangThai") TrangThaiGiaoHang trangThaiGiaoHang){

        long start = System.currentTimeMillis();

        try {
            String tenDangNhap = SecurityContextHolder.getContext().getAuthentication().getName();

            orderService.capNhatTrangThaiDonHang(tenDangNhap, id, trangThaiGiaoHang);

            long time = System.currentTimeMillis() - start;

            perfLogger.info("PUT /don-hang/{id}/trang-thai | user={} | id={} | status={} | time={} ms",
                    tenDangNhap, id, trangThaiGiaoHang, time);

            return ResponseEntity.noContent().build();

        } catch (Exception e) {
            long time = System.currentTimeMillis() - start;

            log.error("ERROR PUT /don-hang/{id}/trang-thai | time={} ms | error={}",
                    time, e.getMessage());

            throw e;
        }
    }

    // ================= GET BY STATUS =================
    @GetMapping("/trang-thai")
    public ResponseEntity<?> getDonHangTheoTrangThai(@RequestParam("trangThai") TrangThaiGiaoHang trangThaiGiaoHang){

        long start = System.currentTimeMillis();

        try {
            String tenDangNhap = SecurityContextHolder.getContext().getAuthentication().getName();

            Object result = orderService.getDonHangTheoTrangThai(tenDangNhap, trangThaiGiaoHang);

            long time = System.currentTimeMillis() - start;

            perfLogger.info("GET /don-hang/trang-thai | user={} | status={} | time={} ms",
                    tenDangNhap, trangThaiGiaoHang, time);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            long time = System.currentTimeMillis() - start;

            log.error("ERROR GET /don-hang/trang-thai | time={} ms | error={}",
                    time, e.getMessage());

            throw e;
        }
    }

    // ================= CANCEL =================
    @PutMapping("/{maDonHang}/huy-don")
    public ResponseEntity<?> huyDonDatHang(@PathVariable("maDonHang") int maDonHang){

        long start = System.currentTimeMillis();

        try {
            String tenDangNhap = SecurityContextHolder.getContext().getAuthentication().getName();

            orderService.thaoTacDonHang(tenDangNhap, maDonHang);

            long time = System.currentTimeMillis() - start;

            perfLogger.info("PUT /don-hang/{id}/huy-don | user={} | id={} | time={} ms",
                    tenDangNhap, maDonHang, time);

            return ResponseEntity.noContent().build();

        } catch (Exception e) {
            long time = System.currentTimeMillis() - start;

            log.error("ERROR PUT /don-hang/{id}/huy-don | time={} ms | error={}",
                    time, e.getMessage());

            throw e;
        }
    }

    // ================= REORDER =================
    @PutMapping("/{maDonHang}/dat-lai")
    public ResponseEntity<?> datLaiDonDatHang(@PathVariable("maDonHang") int maDonHang){

        long start = System.currentTimeMillis();

        try {
            String tenDangNhap = SecurityContextHolder.getContext().getAuthentication().getName();

            orderService.thaoTacDonHang(tenDangNhap, maDonHang);

            long time = System.currentTimeMillis() - start;

            perfLogger.info("PUT /don-hang/{id}/dat-lai | user={} | id={} | time={} ms",
                    tenDangNhap, maDonHang, time);

            return ResponseEntity.noContent().build();

        } catch (Exception e) {
            long time = System.currentTimeMillis() - start;

            log.error("ERROR PUT /don-hang/{id}/dat-lai | time={} ms | error={}",
                    time, e.getMessage());

            throw e;
        }
    }
}