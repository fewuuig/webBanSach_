package com.example.webbansach_backend.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Entity
@Getter
@Setter
@Table(name = "chat_message")
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id")
    private int roomId ;
    @Column(name = "sender")
    private String sender ;
    @Column(name = "content")
    private String content ;
    @Column(name = "timestamp")
    private LocalDateTime timestamp ;
}
