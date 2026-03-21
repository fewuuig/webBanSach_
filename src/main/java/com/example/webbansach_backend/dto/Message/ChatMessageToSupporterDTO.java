package com.example.webbansach_backend.dto.Message;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
