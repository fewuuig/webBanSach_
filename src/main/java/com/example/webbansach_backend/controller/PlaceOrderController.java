package com.example.webbansach_backend.controller;

import com.example.webbansach_backend.dto.DatHangRequestDTO;
import com.example.webbansach_backend.service.OrderService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
public class PlaceOrderController {



    @Autowired
    private OrderService orderService;

    @PostMapping("/place-order")
    public ResponseEntity<?> placeOrder(@RequestBody DatHangRequestDTO datHangRequestDTO) throws JsonProcessingException {
            String tenDangNhap = SecurityContextHolder.getContext().getAuthentication().getName();
            orderService.placeOder(tenDangNhap, datHangRequestDTO);
            return ResponseEntity.noContent().build() ;

    }
}