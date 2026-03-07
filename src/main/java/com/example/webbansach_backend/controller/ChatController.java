package com.example.webbansach_backend.controller;

import com.example.webbansach_backend.Entity.ChatMessage;
import com.example.webbansach_backend.dto.ChatMessageToSupporterDTO;
import com.example.webbansach_backend.dto.ChatMessageToUserDTO;
import com.example.webbansach_backend.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Set;

@Controller
public class ChatController {
    @Autowired
    private SimpMessagingTemplate messagingTemplate ;
    @Autowired
    private ChatService chatService ;
    // lấy lên danh sách các user : tenDangNhap , anhDaiDien , để làm danh sách chat
    @GetMapping("/chat/users")
    public ResponseEntity<?> getListUserChat(){
        return ResponseEntity.ok(chatService.getAllUser()) ;
    }
    @MessageMapping("/chat.send-to-supporter")
    public void sendMessageToSupport(ChatMessageToSupporterDTO message , Principal principal){
        message.setTimestamp(LocalDateTime.now());
        message.setSender(principal.getName());

        // gưỉ đến tất cả supporter(Manager)
        Set<String> usernameOfManager = chatService.getUsernameOfManager() ;
        System.out.println(usernameOfManager.toString());
        usernameOfManager.forEach(usernameManager->{
            messagingTemplate.convertAndSendToUser(usernameManager , "/queue/support" , message);
        });
        messagingTemplate.convertAndSendToUser(principal.getName() , "/queue/support" , message);
    }
    @MessageMapping("/chat.send-to-user")
    public void sendMessageToUser(ChatMessageToUserDTO message , Principal principal){
        message.setTimestamp(LocalDateTime.now());

        Set<String> usernameOfManager = chatService.getUsernameOfManager() ;
        message.setSender(principal.getName());

        messagingTemplate.convertAndSendToUser(message.getSendToUser() , "/queue/support" , message);
        messagingTemplate.convertAndSendToUser(message.getSender() , "/queue/support" , message);

    }
}
