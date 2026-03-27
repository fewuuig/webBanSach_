package com.example.webbansach_backend.service;

import com.example.webbansach_backend.dto.Message.MessageRequestDTO;
import com.example.webbansach_backend.dto.Message.MessageResponeDTO;
import org.springframework.data.redis.connection.stream.MapRecord;

import java.util.List;

public interface ChatMQVer2Service {
    void addMessageToStream(MessageRequestDTO messageRequestDTO) ;
    List<MessageResponeDTO> getMessageOfUser(String sender , String sendToUser , int page , int size) ;
    void processSaveBatch(List<MapRecord<String , Object , Object>> messages );
}
