package com.example.webbansach_backend.controller;
import com.example.webbansach_backend.dto.DatHangFromCartRequestDTO;
import com.example.webbansach_backend.dto.DatHangRequestDTO;

import com.example.webbansach_backend.service.OrderService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping ("/order")
public class PlaceOrderController {
    @Autowired
    private OrderService orderService ;

    @PostMapping("/place-order")
    private ResponseEntity<?> placeOrder(@RequestBody DatHangRequestDTO datHangRequestDTO) throws JsonProcessingException {
        System.out.println(datHangRequestDTO);
        String tenDangNhap = SecurityContextHolder.getContext().getAuthentication().getName();
        orderService.placeOder(  tenDangNhap,datHangRequestDTO);
        return ResponseEntity.ok("đặt hàng thành công") ;
    }

}
