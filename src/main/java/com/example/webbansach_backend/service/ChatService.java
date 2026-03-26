package com.example.webbansach_backend.service;

import com.example.webbansach_backend.dto.Message.AllMessageOfUserRequestDTO;
import com.example.webbansach_backend.dto.Message.MessageRequestDTO;
import com.example.webbansach_backend.dto.Message.MessageResponeDTO;
import com.example.webbansach_backend.dto.NguoiDungChatResponeDTO;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;

public interface ChatService {
    List<NguoiDungChatResponeDTO> getAllUser() ;
    List<NguoiDungChatResponeDTO> getUserListChat();
}
