package com.example.webbansach_backend.service;

import com.example.webbansach_backend.dto.Message.MessageRequestDTO;

public interface ChatMQService {
    void addMessageToStream(MessageRequestDTO messageRequestDTO) ;
}
