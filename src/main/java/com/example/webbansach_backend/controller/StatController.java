package com.example.webbansach_backend.controller;

import com.example.webbansach_backend.dto.stats.StatTodayDTO;
import com.example.webbansach_backend.service.ThongKeBanHangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class StatController {
    @Autowired
    private ThongKeBanHangService thongKeBanHangService ;
    @Autowired
    private SimpMessagingTemplate messagingTemplate ;
//    @MessageMapping("stat-today")
//    public void onStatToday(){
//        Object stat = thongKeBanHangService.onStatsToday() ;
//        if(stat != null){
//            messagingTemplate.convertAndSend("/topic/stats" , stat);
//        }
//    }
}
