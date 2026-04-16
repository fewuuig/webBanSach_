package com.example.webbansach_backend.controller;

import com.example.webbansach_backend.dto.DatHangRequestDTO;
import com.example.webbansach_backend.service.OrderService;
import com.fasterxml.jackson.core.JsonProcessingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/order")
public class PlaceOrderController {

    private static final Logger log = LoggerFactory.getLogger(PlaceOrderController.class);
    private static final Logger perfLogger = LoggerFactory.getLogger("PERF_LOGGER");

    @Autowired
    private OrderService orderService;

    @PostMapping("/place-order")
    public ResponseEntity<?> placeOrder(@RequestBody DatHangRequestDTO datHangRequestDTO)
            throws JsonProcessingException {

        long start = System.currentTimeMillis();

        try {
            String tenDangNhap = SecurityContextHolder.getContext().getAuthentication().getName();

            // 🔥 BUSINESS LOG (không log full object tránh leak data)
            log.info("PLACE ORDER START | user={} | items={} | voucherId={}",
                    tenDangNhap,
                    datHangRequestDTO.getItems() != null
                            ? datHangRequestDTO.getItems().size()
                            : 0 ,
                    datHangRequestDTO.getMaGiam()
            );

            orderService.placeOder(tenDangNhap, datHangRequestDTO);

            long time = System.currentTimeMillis() - start;

            perfLogger.info("POST /order/place-order | user={} | time={} ms",
                    tenDangNhap, time);

            return ResponseEntity.ok("Đặt hàng thành công");

        } catch (Exception e) {

            long time = System.currentTimeMillis() - start;

            log.error("ERROR POST /order/place-order | time={} ms | error={}",
                    time, e.getMessage());

            throw e;
        }
    }
}