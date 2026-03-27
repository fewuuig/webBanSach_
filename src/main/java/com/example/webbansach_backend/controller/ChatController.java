package com.example.webbansach_backend.controller;

import com.example.webbansach_backend.dto.Message.*;
import com.example.webbansach_backend.service.ChatMQService;
import com.example.webbansach_backend.service.ChatMQVer2Service;
import com.example.webbansach_backend.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Set;

@Controller
public class ChatController {
    @Autowired
    private ChatService chatService ;

    @Autowired
    private ChatMQVer2Service chatMQVer2Service ;
    @GetMapping("/chat/users")
    public ResponseEntity<?> getListUserChat(){
        return ResponseEntity.ok(chatService.getAllUser()) ;
    }
    @GetMapping("chat/list-users")
    public ResponseEntity<?> getGetListUserChat2(){
        return ResponseEntity.ok(chatService.getUserListChat()) ;
    }

    @MessageMapping("/chat/users/dm")
    public ResponseEntity<?> insertDBAndSend(@RequestBody MessageRequestDTO messageRequestDTO){
//        chatService.insertDBAndSend(messageRequestDTO) ;
        chatMQVer2Service.addMessageToStream(messageRequestDTO);
        return ResponseEntity.noContent().build() ;
    }
    @GetMapping("chat/users/dm/messages")
    public ResponseEntity<?> getMessageOfUser(@RequestParam("sender") String sender ,
                                              @RequestParam("sendToUser") String sendToUser,
                                              @RequestParam("page") int page ,
                                              @RequestParam("size") int size){
        return ResponseEntity.ok(chatMQVer2Service.getMessageOfUser(sender , sendToUser , page , size)) ;
    }
}
