package com.example.webbansach_backend.controller;

import com.example.webbansach_backend.dto.AddToCartRequestDTO;
import com.example.webbansach_backend.dto.DeleteBookToCartRequestDTO;
import com.example.webbansach_backend.dto.UpdateQuantityDTO;
import com.example.webbansach_backend.service.CartService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// shopping cart
@RestController
@RequestMapping("/cart")
public class CartController {

    private static final Logger log = LoggerFactory.getLogger(CartController.class);

    // 🔥 PERF LOGGER
    private static final Logger perfLogger = LoggerFactory.getLogger("PERF_LOGGER");

    @Autowired
    private CartService cartService;

    // ================= ADD =================
    @PostMapping("/add-to-cart")
    public ResponseEntity<?> addItemIntoCart(@RequestBody AddToCartRequestDTO addToCartRequestDTO){

        long start = System.currentTimeMillis();

        try {
            String tenDangNhap = SecurityContextHolder.getContext().getAuthentication().getName();

            cartService.addToCart(tenDangNhap, addToCartRequestDTO);

            long time = System.currentTimeMillis() - start;

            perfLogger.info("POST /cart/add-to-cart | user={} | bookId={} | quantity={} | time={} ms",
                    tenDangNhap,
                    addToCartRequestDTO.getMaSach(),
                    addToCartRequestDTO.getSoLuong(),
                    time);

            return ResponseEntity.ok("Thêm vào giỏ hàng thành công");

        } catch (Exception e) {
            long time = System.currentTimeMillis() - start;

            log.error("ERROR POST /cart/add-to-cart | time={} ms | error={}",
                    time, e.getMessage());

            throw e;
        }
    }

    // ================= VIEW =================
    @GetMapping("/view-cart")
    public ResponseEntity<?> viewCart(){

        long start = System.currentTimeMillis();

        try {
            String tenDangNhap = SecurityContextHolder.getContext().getAuthentication().getName();

            Object result = cartService.viewCart(tenDangNhap);

            long time = System.currentTimeMillis() - start;

            perfLogger.info("GET /cart/view-cart | user={} | time={} ms",
                    tenDangNhap, time);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            long time = System.currentTimeMillis() - start;

            log.error("ERROR GET /cart/view-cart | time={} ms | error={}",
                    time, e.getMessage());

            throw e;
        }
    }

    // ================= GET SELECTED =================
    @GetMapping("/sach-dat-tu-gio-hang")
    public ResponseEntity<?> getSachDatTuGio(@RequestParam List<Long> danhSachSanPhamDatHangTuGio){

        long start = System.currentTimeMillis();

        try {
            Object result = cartService.getSachDatTuGio(danhSachSanPhamDatHangTuGio);

            long time = System.currentTimeMillis() - start;

            perfLogger.info("GET /cart/sach-dat-tu-gio-hang | ids={} | time={} ms",
                    danhSachSanPhamDatHangTuGio, time);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            long time = System.currentTimeMillis() - start;

            log.error("ERROR GET /cart/sach-dat-tu-gio-hang | time={} ms | error={}",
                    time, e.getMessage());

            throw e;
        }
    }

    // ================= UPDATE =================
    @PutMapping("/update-quantity")
    public ResponseEntity<?> updateQuantity(@RequestBody UpdateQuantityDTO updateQuantityDTO){

        long start = System.currentTimeMillis();

        try {
            String tenDangNhap = SecurityContextHolder.getContext().getAuthentication().getName();

            cartService.updateQuantity(
                    tenDangNhap,
                    updateQuantityDTO.getMaSach(),
                    updateQuantityDTO.getSoLuong()
            );

            long time = System.currentTimeMillis() - start;

            perfLogger.info("PUT /cart/update-quantity | user={} | bookId={} | quantity={} | time={} ms",
                    tenDangNhap,
                    updateQuantityDTO.getMaSach(),
                    updateQuantityDTO.getSoLuong(),
                    time);

            return ResponseEntity.ok("Cập nhật số lượng thành công");

        } catch (Exception e) {
            long time = System.currentTimeMillis() - start;

            log.error("ERROR PUT /cart/update-quantity | time={} ms | error={}",
                    time, e.getMessage());

            throw e;
        }
    }

    // ================= DELETE =================
    @DeleteMapping("/delete-book")
    public ResponseEntity<?> deleteBook(@RequestBody DeleteBookToCartRequestDTO deleteBookToCartRequestDTO){

        long start = System.currentTimeMillis();

        try {
            cartService.deleteBook(deleteBookToCartRequestDTO.getDanhSachSanPhamChon());

            long time = System.currentTimeMillis() - start;

            perfLogger.info("DELETE /cart/delete-book | ids={} | time={} ms",
                    deleteBookToCartRequestDTO.getDanhSachSanPhamChon(),
                    time);

            return ResponseEntity.ok("Xóa sách khỏi giỏ hàng thành công");

        } catch (Exception e) {
            long time = System.currentTimeMillis() - start;

            log.error("ERROR DELETE /cart/delete-book | time={} ms | error={}",
                    time, e.getMessage());

            throw e;
        }
    }

    // ================= CLEAR =================
    @DeleteMapping("/clear-book_cart")
    public ResponseEntity<?> clearBookInCart(){

        long start = System.currentTimeMillis();

        try {
            String tenDangNhap = SecurityContextHolder.getContext().getAuthentication().getName();

            cartService.clearBookFromCart(tenDangNhap);

            long time = System.currentTimeMillis() - start;

            perfLogger.info("DELETE /cart/clear-book_cart | user={} | time={} ms",
                    tenDangNhap, time);

            return ResponseEntity.ok("Xóa toàn bộ giỏ hàng thành công");

        } catch (Exception e) {
            long time = System.currentTimeMillis() - start;

            log.error("ERROR DELETE /cart/clear-book_cart | time={} ms | error={}",
                    time, e.getMessage());

            throw e;
        }
    }
}