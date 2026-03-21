package com.example.webbansach_backend.controller;

import com.example.webbansach_backend.dto.stats.StatTodayDTO;
import com.example.webbansach_backend.service.ThongKeBanHangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/stats")
public class StatController {
    @Autowired
    private ThongKeBanHangService thongKeBanHangService ;
    @GetMapping("/statToday")
    public ResponseEntity<?> statToday(){
        return ResponseEntity.ok(thongKeBanHangService.getStatToday()) ;
    }
}
