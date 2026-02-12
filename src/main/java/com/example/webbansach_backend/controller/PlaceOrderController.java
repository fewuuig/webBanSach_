package com.example.webbansach_backend.controller;
import com.example.webbansach_backend.dto.DatHangFromCartRequestDTO;
import com.example.webbansach_backend.dto.DatHangRequestDTO;

import com.example.webbansach_backend.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping ("/order")
public class PlaceOrderController {
    @Autowired
    private OrderService orderService ;

    @PostMapping("/place-order-from-cart")
    private ResponseEntity<?> placeOrderFromCart(@RequestBody DatHangFromCartRequestDTO datHangFromCartRequestDTO){
        String tenDangNhap = SecurityContextHolder.getContext().getAuthentication().getName();
        orderService.placeOrderFromCart(tenDangNhap,datHangFromCartRequestDTO);
        return ResponseEntity.ok("đặt hàng thành công") ;
    }
    @PostMapping("/place-order")
    private ResponseEntity<?> placeOrder(@RequestBody DatHangRequestDTO datHangRequestDTO){
        String tenDangNhap = SecurityContextHolder.getContext().getAuthentication().getName();
        orderService.placeOder(tenDangNhap, datHangRequestDTO);
        System.out.println("dat hang thanh cong");
        return ResponseEntity.ok("đặt hàng thành công") ;
    }

}
