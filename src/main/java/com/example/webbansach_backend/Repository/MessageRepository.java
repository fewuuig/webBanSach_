package com.example.webbansach_backend.Repository;

import com.example.webbansach_backend.Entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;

public interface MessageRepository extends JpaRepository<Message , Long> {
    @Query(value = """
        SELECT ms
        FROM Message ms
        WHERE ms.room.roomId =:roomId
    """)
    Page<Message> findByRoom_RoomId(@Param("roomId") int roomId , Pageable pageable) ;
    boolean existsByCreatedAtAndContent(Instant timestamp , String content) ; // index for content
}
