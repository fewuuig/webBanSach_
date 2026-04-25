package com.example.webbansach_backend.service;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface SseService {
    SseEmitter subscribe(String tenDangNhap) ;
    void sendNotifycation(String tenDanggNhap , String message) ;
}
