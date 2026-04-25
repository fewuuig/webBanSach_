package com.example.webbansach_backend.service.impl;

import com.example.webbansach_backend.service.OrderService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class CompensateStockRedisService {
    @Autowired
    private OrderService orderService ;
    // mở 8 luồng
    @Scheduled(fixedDelay = 5*60*1000)
    public void compensate() throws JsonProcessingException {
        for(int i = 0 ; i< 8 ; i++){
            taskCompensate(i);
        }
    }
    @Async
    public void taskCompensate(int shard) throws JsonProcessingException {
        orderService.compensateStockRedisBook(shard);
    }


}
