package com.example.webbansach_backend.service.impl;

import com.example.webbansach_backend.Repository.DonHangRepository;
import com.example.webbansach_backend.service.ReturnOrderTimeoutBatchService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class ReturnOrderTimeoutBatchServiceImpl implements ReturnOrderTimeoutBatchService {
    @Autowired
    private RedisTemplate<String , Object> redisTemplate ;
    @Autowired
    @Qualifier("returnOrderTimeout")
    private DefaultRedisScript<List> returnOrderTimeout ;
    @Autowired
    private DonHangRepository donHangRepository;
    public void addOrderTimeout(int orderId){
        String key = "order_timeout_queue" ;
        long delayTime = System.currentTimeMillis() + 172800000 ;
        redisTemplate.opsForZSet().add(key , orderId ,delayTime) ;
    }

    // sử lý đơn hàng bị qúa thời gian CHO_XAC_NHAN (timeout)
    @Transactional
    @Scheduled(fixedDelay = 30000)
    public void processOrderTimeout(){
        String key = "order_timeout_queue" ;
        List<String> keys = Arrays.asList(key) ;
        long now = System.currentTimeMillis() ;
        List<Object> orderIds  = redisTemplate.execute(returnOrderTimeout , keys , now ) ;
        // hoàn kho
        donHangRepository.returnOrderTimeoutIntoStock(orderIds);
        // update state order timeout
        donHangRepository.updateStateOrderTimeout(orderIds); //updateState = CHO_XAC_NHAN ->  DA_HUY


    }
}
