package com.example.webbansach_backend.service.impl;

import com.example.webbansach_backend.service.OrderService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class RetryOrderServie {
    @Autowired
    private RedisTemplate<String , Object> redisTemplate ;
    @Autowired
    private OrderService orderService ;


    @Scheduled(fixedDelay = 5*60*1000)
    public void retryOrder() throws JsonProcessingException {
        // chia ra 8 luồng
        List<CompletableFuture<Void>> futures = new ArrayList<>() ;
        for(int i = 0 ;i<8 ; i++){
            futures.add(retryOrderWorker(i));
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }

    @Async
    public CompletableFuture<Void> retryOrderWorker(int shard) throws JsonProcessingException {
        PendingMessagesSummary pendingMessagesSummary = redisTemplate.opsForStream().pending(
                "order-stream:{ws}:shard-"+shard,
                "group:shard-"+shard
        ) ;
        if(pendingMessagesSummary == null|| pendingMessagesSummary.getTotalPendingMessages() == 0) return CompletableFuture.completedFuture(null);

        PendingMessages pendingMessages = redisTemplate.opsForStream().pending(
                "order-stream:{ws}:shard-"+shard ,
                Consumer.from("group:shard-"+shard , "consumer-1") ,
                Range.unbounded() , 300
        ) ;

        // tạo 1 list message để xử lý theo lô
        List<MapRecord<String , Object ,Object>> messages = new ArrayList<>( );
        for(PendingMessage pendingMessage : pendingMessages){
            if(pendingMessage.getTotalDeliveryCount() >=6){
                List<MapRecord<String , Object,Object>> claimed  = redisTemplate.opsForStream().claim(
                        "order-stream:{ws}:shard-"+shard ,
                        "group:shard-"+shard ,
                        "consumer-1" ,
                        Duration.ofSeconds(30) ,
                        pendingMessage.getId()
                );
                // lưu vào stream dead-letter để sưe lý sau
                for(MapRecord<String , Object ,Object> message : claimed){
                    redisTemplate.opsForStream()
                            .add(StreamRecords.mapBacked(message.getValue()).withStreamKey("order-stream-dead:{shard-"+shard+"}")) ;
                }
                // ACK để xóa nó khỏi PEL của voucher-stream -> voucher-group
                redisTemplate.opsForStream().acknowledge(
                        "order-stream:{ws}:shard-"+shard,
                        "group:shard-"+shard,
                        pendingMessage.getId()
                );
                continue;
            }

            if(pendingMessage.getElapsedTimeSinceLastDelivery().toSeconds() >= 30){
                List<MapRecord<String , Object,Object>> claimed  = redisTemplate.opsForStream().claim(
                        "order-stream:{ws}:shard-"+shard ,
                        "group:shard-"+shard ,
                        "consumer-1" ,
                        Duration.ofSeconds(2) ,
                        pendingMessage.getId()
                );
                // add message ở đây
                messages.addAll(claimed);
            }
        }
        if(messages.isEmpty()) return CompletableFuture.completedFuture(null);
        orderService.saveBatch(messages,shard);
        return CompletableFuture.completedFuture(null) ;
    }

}
