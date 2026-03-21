package com.example.webbansach_backend.Entity;

import com.example.webbansach_backend.Enum.MessageStatus;
import com.example.webbansach_backend.Enum.MessageType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Table(name = "message")
@Getter
@Setter
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private long messageId ;

    @Column(name = "content" , columnDefinition = "LONGTEXT" ,nullable = false)
    @Lob
    private String content ;

    @Column(name = "type" , nullable = false)
    @Enumerated(EnumType.STRING)
    private MessageType type ;

    @Column(name = "status" , nullable = false)
    @Enumerated(EnumType.STRING)
    private MessageStatus status ;

    @Column(name = "created_at")
    private Instant createdAt ;

    @ManyToOne
    @JoinColumn(name = "room_id" )
    private Room room ;

    @ManyToOne
    @JoinColumn(name = "ma_nguoi_dung")
    private NguoiDung nguoiDung ;



}
