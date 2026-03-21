package com.example.webbansach_backend.Entity;

import com.example.webbansach_backend.Enum.RoleRoomChat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "room_nguoi_dung")
@Getter
@Setter
public class RoomNguoiDung {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_nguoi_dung_id")
    private int roomNguoiDungId ;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private RoleRoomChat role ;

    @Column(name = "joined_at")
    private LocalDateTime joinedAt ;

    @ManyToOne
    @JoinColumn(name = "room_id" , nullable = false)
    private Room room;

    @ManyToOne
    @JoinColumn(name = "ma_nguoi_dung" , nullable = false)
    private NguoiDung nguoiDung ;



}
