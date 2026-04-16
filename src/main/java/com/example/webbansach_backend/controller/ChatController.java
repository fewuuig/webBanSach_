package com.example.webbansach_backend.controller;

import com.example.webbansach_backend.dto.Message.*;
import com.example.webbansach_backend.service.ChatMQVer2Service;
import com.example.webbansach_backend.service.ChatService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class ChatController {

    private static final Logger log = LoggerFactory.getLogger(ChatController.class);

    // 🔥 PERF LOGGER
    private static final Logger perfLogger = LoggerFactory.getLogger("PERF_LOGGER");

    @Autowired
    private ChatService chatService;

    @Autowired
    private ChatMQVer2Service chatMQVer2Service;

    // ================= GET USERS =================
    @GetMapping("/chat/users")
    public ResponseEntity<?> getListUserChat(){

        long start = System.currentTimeMillis();

        try {
            Object result = chatService.getAllUser();

            long time = System.currentTimeMillis() - start;

            perfLogger.info("GET /chat/users | time={} ms", time);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            long time = System.currentTimeMillis() - start;

            log.error("ERROR GET /chat/users | time={} ms | error={}",
                    time, e.getMessage());

            throw e;
        }
    }

    // ================= GET LIST CHAT =================
    @GetMapping("/chat/list-users")
    public ResponseEntity<?> getGetListUserChat2(){

        long start = System.currentTimeMillis();

        try {
            Object result = chatService.getUserListChat();

            long time = System.currentTimeMillis() - start;

            perfLogger.info("GET /chat/list-users | time={} ms", time);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            long time = System.currentTimeMillis() - start;

            log.error("ERROR GET /chat/list-users | time={} ms | error={}",
                    time, e.getMessage());

            throw e;
        }
    }

    // ================= SEND MESSAGE (WS) =================
    @MessageMapping("/chat/users/dm")
    public void insertDBAndSend(@RequestBody MessageRequestDTO messageRequestDTO){

        long start = System.currentTimeMillis();

        try {
            chatMQVer2Service.addMessageToStream(messageRequestDTO);

            long time = System.currentTimeMillis() - start;

            perfLogger.info("WS /chat/users/dm | sender={} | receiver={} | time={} ms",
                    messageRequestDTO.getSender(),
                    messageRequestDTO.getSendToUser(),
                    time);

        } catch (Exception e) {
            long time = System.currentTimeMillis() - start;

            log.error("ERROR WS /chat/users/dm | time={} ms | error={}",
                    time, e.getMessage());

            throw e;
        }
    }

    // ================= GET MESSAGE =================
    @GetMapping("/chat/users/dm/messages")
    public ResponseEntity<?> getMessageOfUser(@RequestParam("sender") String sender,
                                              @RequestParam("sendToUser") String sendToUser,
                                              @RequestParam("page") int page,
                                              @RequestParam("size") int size){

        long start = System.currentTimeMillis();

        try {
            Object result = chatMQVer2Service.getMessageOfUser(sender, sendToUser, page, size);

            long time = System.currentTimeMillis() - start;

            perfLogger.info("GET /chat/users/dm/messages | sender={} | receiver={} | page={} | size={} | time={} ms",
                    sender, sendToUser, page, size, time);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            long time = System.currentTimeMillis() - start;

            log.error("ERROR GET /chat/users/dm/messages | time={} ms | error={}",
                    time, e.getMessage());

            throw e;
        }
    }
}