package com.example.webbansach_backend.service.impl;

import com.example.webbansach_backend.service.SseService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SseServiceImpl implements SseService {
    // luư nó vào map
    private Map<String , SseEmitter> sseEmitters = new ConcurrentHashMap<>();
    @Override
    public SseEmitter subscribe(String tenDangNhap) {
        // cấu hình thười gian cho sse của mỗi user chờ : tầm 1p
        SseEmitter sseEmitter = new SseEmitter(60*1000L) ;
        // thêm vào map để thông báo
        sseEmitters.put(tenDangNhap , sseEmitter) ;

        // cấu hình dể tối ưu
        // nếu mất kết nối , lỗi , timeout (thao tác cài đặt cho mỗi emitter)
        sseEmitter.onCompletion(()->sseEmitters.remove(tenDangNhap));
        sseEmitter.onError((e)-> {
            sseEmitter.complete(); // ngắt kết nối luôn
            sseEmitters.remove(tenDangNhap);
        });
        sseEmitter.onTimeout(()-> {
            sseEmitter.complete(); // đón kết nối luôn
            sseEmitters.remove(tenDangNhap);
        });

        try {
            sseEmitter.send(SseEmitter.event().name("ORDER_RESULT").data("kết nối thành công"));
        }catch (IOException ex){
            sseEmitters.remove(tenDangNhap) ;
        }
        return sseEmitter;
    }

    // thưc hiện gửi tin nhắn
    @Override
    public void sendNotifycation(String tenDanggNhap, String message) {
        SseEmitter emitter = sseEmitters.get(tenDanggNhap) ;
        if(emitter != null){
           try {
               emitter.send(SseEmitter.event().name("NOTIFiCATION").data(message));
               emitter.complete();
           }catch (IOException ex){
               sseEmitters.remove(tenDanggNhap) ;
           }
        }
    }
}
