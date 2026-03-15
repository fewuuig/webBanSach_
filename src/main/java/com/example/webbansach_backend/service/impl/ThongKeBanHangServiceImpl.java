package com.example.webbansach_backend.service.impl;

import com.example.webbansach_backend.dto.stats.StatTodayDTO;
import com.example.webbansach_backend.service.ThongKeBanHangService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class ThongKeBanHangServiceImpl implements ThongKeBanHangService {
    @Autowired
    private RedisTemplate<String , Object> redisTemplate ;
    @Autowired
    private SimpMessagingTemplate messagingTemplate ;
    Integer orders = null ;
    Integer books = null ;
    Double revenue = null ;
    @Override
    @Transactional
    public void  onStatsToday() {
        String key = "stats:" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        try {
             orders = Integer.parseInt(redisTemplate.opsForHash().get(key, "orders").toString());
             books = Integer.parseInt(redisTemplate.opsForHash().get(key, "books").toString());
             revenue = Double.parseDouble(redisTemplate.opsForHash().get(key, "revenue").toString());
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            throw ex;
        }

        if(orders == null || books == null || revenue == null) return ;

        StatTodayDTO statTodayDTO = new StatTodayDTO();
        statTodayDTO.setRevenue(revenue);
        statTodayDTO.setOrders(orders);
        statTodayDTO.setBooks(books);

        messagingTemplate.convertAndSend("/topic/stats" , statTodayDTO);
    }
}
