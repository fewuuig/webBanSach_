package com.example.webbansach_backend.service.impl;

import com.example.webbansach_backend.Entity.NguoiDung;
import com.example.webbansach_backend.Repository.NguoiDungRepository;
import com.example.webbansach_backend.dto.NguoiDungChatResponeDTO;
import com.example.webbansach_backend.mapper.NguoiDungMapper;
import com.example.webbansach_backend.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatServiceImpl implements ChatService {
    @Autowired
    private NguoiDungRepository nguoiDungRepository ;
    @Autowired
    private NguoiDungMapper nguoiDungMapper ;
    
    @Override
    public List<NguoiDungChatResponeDTO> getAllUser() {
        return null;
    }

    public List<NguoiDungChatResponeDTO> getUserListChat(){
        List<NguoiDung> nguoiDungs = nguoiDungRepository.findAll() ;
        return nguoiDungs.stream().map(nguoiDungMapper::toDTO).toList() ;
    }

}
