package com.example.webbansach_backend.dto.Message;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ChatMessageToSupporterDTO {
    private String sender ;
    private String content ;
    private LocalDateTime timestamp ;
}
