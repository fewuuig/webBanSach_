package com.example.webbansach_backend.controller;

import com.example.webbansach_backend.dto.AddWishLoveRequestDTO;
import com.example.webbansach_backend.service.WishLoveService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/wish-love")
public class DanhSachYeuThichController {

    private static final Logger log = LoggerFactory.getLogger(DanhSachYeuThichController.class);

    // 🔥 PERF LOGGER
    private static final Logger perfLogger = LoggerFactory.getLogger("PERF_LOGGER");

    @Autowired
    private WishLoveService wishLoveService;

    // ================= GET LIST =================
    @GetMapping("/danh-sach-yeu-thich")
    public ResponseEntity<?> getWishLoveList(){

        long start = System.currentTimeMillis();

        try {
            String tenDangNhap = SecurityContextHolder.getContext().getAuthentication().getName();

            Object result = wishLoveService.getWishLoveList(tenDangNhap);

            long time = System.currentTimeMillis() - start;

            perfLogger.info("GET /wish-love/danh-sach-yeu-thich | user={} | time={} ms",
                    tenDangNhap, time);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            long time = System.currentTimeMillis() - start;

            log.error("ERROR GET /wish-love/danh-sach-yeu-thich | time={} ms | error={}",
                    time, e.getMessage());

            throw e;
        }
    }

    // ================= ADD =================
    @PostMapping("/add-to-wish-love")
    public ResponseEntity<?> addWishLoveList(@RequestBody AddWishLoveRequestDTO addWishLoveRequestDTO){

        long start = System.currentTimeMillis();

        try {
            String tenDangNhap = SecurityContextHolder.getContext().getAuthentication().getName();

            wishLoveService.addWishLoveList(tenDangNhap, addWishLoveRequestDTO.getMaSach());

            long time = System.currentTimeMillis() - start;

            perfLogger.info("POST /wish-love/add-to-wish-love | user={} | bookId={} | time={} ms",
                    tenDangNhap, addWishLoveRequestDTO.getMaSach(), time);

            return ResponseEntity.ok("Thêm sách vào danh sách yêu thích thành công");

        } catch (Exception e) {
            long time = System.currentTimeMillis() - start;

            log.error("ERROR POST /wish-love/add-to-wish-love | time={} ms | error={}",
                    time, e.getMessage());

            throw e;
        }
    }

    // ================= CHECK =================
    @GetMapping("/check")
    public ResponseEntity<?> checkWishLove(@RequestParam("maSach") int maSach){

        long start = System.currentTimeMillis();

        try {
            String tenDangNhap = SecurityContextHolder.getContext().getAuthentication().getName();

            Object result = wishLoveService.check(tenDangNhap, maSach);

            long time = System.currentTimeMillis() - start;

            perfLogger.info("GET /wish-love/check | user={} | bookId={} | time={} ms",
                    tenDangNhap, maSach, time);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            long time = System.currentTimeMillis() - start;

            log.error("ERROR GET /wish-love/check | time={} ms | error={}",
                    time, e.getMessage());

            throw e;
        }
    }
}