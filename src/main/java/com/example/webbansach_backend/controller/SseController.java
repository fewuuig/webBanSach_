package com.example.webbansach_backend.controller;

import com.example.webbansach_backend.service.SseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/sse")
public class SseController {
    @Autowired
    private SseService sseService ;

    @GetMapping(value = "/order/subscribe" ,produces = MediaType.TEXT_EVENT_STREAM_VALUE) // giúp giữu kết nối để trả về dữ liệu liên tục thay vì trả về respone thong thường rồi đóng
    public SseEmitter subcribe(){
        String tenDangNhap = SecurityContextHolder.getContext().getAuthentication().getName() ;
        return sseService.subscribe(tenDangNhap) ;
    }

}
