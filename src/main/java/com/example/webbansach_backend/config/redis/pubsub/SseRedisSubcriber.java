package com.example.webbansach_backend.config.redis.pubsub;

import com.example.webbansach_backend.service.SseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SseRedisSubcriber {
    @Autowired
    private SseService sseService ;

    public void onMessage(String message , String channel){
        // username:message
        String[] parts = message.split(":" , 2) ;
        if(parts.length == 2){
            String username = parts[0] ;
            String content = parts[1] ;

            sseService.sendNotifycation(username , content);
        }
    }

}
