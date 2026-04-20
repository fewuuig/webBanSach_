package com.example.webbansach_backend.service;

import com.example.webbansach_backend.dto.NguoiDungChatResponeDTO;

import java.util.List;

public interface ChatService {
    List<NguoiDungChatResponeDTO> getAllUser() ;
    List<NguoiDungChatResponeDTO> getUserListChat();
}
