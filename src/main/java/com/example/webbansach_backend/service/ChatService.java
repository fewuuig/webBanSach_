package com.example.webbansach_backend.service;

import com.example.webbansach_backend.dto.Message.AllMessageOfUserRequestDTO;
import com.example.webbansach_backend.dto.Message.MessageRequestDTO;
import com.example.webbansach_backend.dto.Message.MessageResponeDTO;
import com.example.webbansach_backend.dto.NguoiDungChatResponeDTO;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;

public interface ChatService {
    Set<String> getUsernameOfManager() ;
    List<NguoiDungChatResponeDTO> getAllUser() ;
    void insertDBAndSend(MessageRequestDTO message) ;
    List<MessageResponeDTO> getMessageOfUser(String sender , String sendToUser , int page , int szie) ;
    List<NguoiDungChatResponeDTO> getUserListChat();
}
