package com.example.webbansach_backend.service;

import com.example.webbansach_backend.dto.NguoiDungChatResponeDTO;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;

public interface ChatService {
    Set<String> getUsernameOfManager() ;
    List<NguoiDungChatResponeDTO> getAllUser() ;
}
