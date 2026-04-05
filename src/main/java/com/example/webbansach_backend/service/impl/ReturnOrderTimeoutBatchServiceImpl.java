package com.example.webbansach_backend.service.impl;

import com.example.webbansach_backend.Repository.DonHangRepository;
import com.example.webbansach_backend.service.ReturnOrderTimeoutBatchService;
import com.example.webbansach_backend.utils.ParseListUtil;
import com.example.webbansach_backend.utils.TimeLogUtil;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    @Scheduled(fixedDelay = 172800000)
    public void processOrderTimeout(){
        String key = "order_timeout_queue" ;
        List<String> keys = Arrays.asList(key) ;
        long now = System.currentTimeMillis() ;
        List<Object> orderIds  = redisTemplate.execute(returnOrderTimeout , keys , now ) ;
        if(orderIds == null || orderIds.isEmpty()) return ;
        // hoan kho ram
//        Map<String , Integer> map = new HashMap<>() ;
//        orderIds.forEach(id->{
//            map.put("book:"+id , )
//        });
//        redisTemplate.opsForValue().multiSet(keyBook);
        // hoàn kho DB
        donHangRepository.returnOrderTimeoutIntoStock(orderIds);
        // update state order timeout
        donHangRepository.updateStateOrderTimeout(orderIds); //updateState = CHO_XAC_NHAN ->  DA_HUY
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                System.out.println("["+ TimeLogUtil.toTimeSystemLog() +"]"+"Đã cập nhật trang thái đơn & hoàn kho:"+orderIds);
            }
        });

    }
    // chỗ mày xc cần phải hoàn lại kho ở redis mới hợp lý
}
