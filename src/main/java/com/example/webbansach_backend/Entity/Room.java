package com.example.webbansach_backend.Entity;

import com.example.webbansach_backend.Enum.RoomType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "room")
@Getter
@Setter
public class Room {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column(name = "room_id")
    private int roomId ;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private RoomType type ;

    @Column(name = "name")
    private String name ;

    @OneToMany(mappedBy = "room",fetch = FetchType.LAZY , cascade = CascadeType.ALL)
    private List<RoomNguoiDung> roomNguoiDungs = new ArrayList<>() ;

    @OneToMany(mappedBy = "room" , fetch = FetchType.LAZY ,cascade = CascadeType.ALL)
    private List<Message> messages = new ArrayList<>();
}
