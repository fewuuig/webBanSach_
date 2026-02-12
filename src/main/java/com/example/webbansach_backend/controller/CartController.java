package com.example.webbansach_backend.controller;

import com.example.webbansach_backend.Entity.GioHang;
import com.example.webbansach_backend.Entity.GioHangSach;
import com.example.webbansach_backend.dto.AddToCartRequestDTO;
import com.example.webbansach_backend.dto.DeleteBookToCartRequestDTO;
import com.example.webbansach_backend.dto.UpdateQuantityDTO;
import com.example.webbansach_backend.service.CartService;
import com.example.webbansach_backend.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// shopping cart
@RestController
@RequestMapping("/cart")
public class CartController {
    @Autowired
    private OrderService orderService ;
    @Autowired
    private CartService cartService ;
    @PostMapping("/add-to-cart")
    private ResponseEntity<?> addItemIntoCart(@RequestBody AddToCartRequestDTO addToCartRequestDTO){
        String tenDangNhap = SecurityContextHolder.getContext().getAuthentication().getName() ;
        cartService.addToCart(tenDangNhap , addToCartRequestDTO);
        return ResponseEntity.ok("thêm vào giỏ hàng thành công");
    }
    @GetMapping("/view-cart")
    private ResponseEntity<?> viewCart(){
        String tenDangNhap = SecurityContextHolder.getContext().getAuthentication().getName() ;
        System.out.println("xem giỏ hàng thành công");
        return ResponseEntity.ok( cartService.viewCart(tenDangNhap));
    }
    @GetMapping("/sach-dat-tu-gio-hang")
    private ResponseEntity<?> getSachDatTuGio(@RequestParam List<Long> danhSachSanPhamDatHangTuGio){
        return ResponseEntity.ok(cartService.getSachDatTuGio(danhSachSanPhamDatHangTuGio)) ;
    }
    @PutMapping("/update-quantity")
    private ResponseEntity<?> updateQuantity(@RequestBody UpdateQuantityDTO updateQuantityDTO){
        String tenDangNhap = SecurityContextHolder.getContext().getAuthentication().getName() ;
        cartService.updateQuantity(tenDangNhap , updateQuantityDTO.getMaSach() , updateQuantityDTO.getSoLuong());
        return ResponseEntity.ok("Cập nhật số lượng thành công") ;
    }
    @DeleteMapping("/delete-book")
    private ResponseEntity<?> deleteBook(@RequestBody DeleteBookToCartRequestDTO deleteBookToCartRequestDTO){
        String tenDangNhap = SecurityContextHolder.getContext().getAuthentication().getName();
        cartService.deleteBook( deleteBookToCartRequestDTO.getDanhSachSanPhamChon());
        System.out.println("xóa sách khỏi giỏ hàng thành công " + deleteBookToCartRequestDTO.getDanhSachSanPhamChon());
        return ResponseEntity.ok("xóa sách khỏi giỏ hàng thành công") ;
    }
    @DeleteMapping("/clear-book_cart")
    private ResponseEntity<?> clearBookInCart(){
        String tenDangNhap = SecurityContextHolder.getContext().getAuthentication().getName();
        cartService.clearBookFromCart(tenDangNhap);
        return ResponseEntity.ok("xóa tất cả sách khỏi giỏ hàng thành công") ;
    }

}
