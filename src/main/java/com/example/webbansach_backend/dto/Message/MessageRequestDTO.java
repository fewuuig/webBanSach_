package com.example.webbansach_backend.dto.Message;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class MessageRequestDTO {
    private String content ;
    private String sender ;
    private String sendToUser ;

    @Override
    public String toString() {
        return "MessageRequestDTO{" +
                "content='" + content + '\'' +
                ", sender='" + sender + '\'' +
                ", sendToUser='" + sendToUser + '\'' +
                '}';
    }
}
