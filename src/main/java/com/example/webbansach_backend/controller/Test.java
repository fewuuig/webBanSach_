package com.example.webbansach_backend.controller;

import com.example.webbansach_backend.dto.Message.MessageRequestDTO;
import com.example.webbansach_backend.service.ChatMQVer2Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class Test {
    @Autowired
    private ChatMQVer2Service chatMQVer2Service ;

    @PostMapping("/test/dm")
    public ResponseEntity<?> sendMess(@RequestBody MessageRequestDTO messageRequestDTO){

        long start = System.nanoTime();

        chatMQVer2Service.addMessageToStream(messageRequestDTO);

        long end = System.nanoTime();

        System.out.println("⏱ Controller return time: " + ((end - start) / 1_000_000.0) + " ms");

        return ResponseEntity.noContent().build();
    }


}
