package com.example.webbansach_backend.service.impl;

import com.example.webbansach_backend.Entity.Message;
import com.example.webbansach_backend.Entity.NguoiDung;
import com.example.webbansach_backend.Entity.Room;
import com.example.webbansach_backend.Entity.RoomNguoiDung;
import com.example.webbansach_backend.Enum.MessageStatus;
import com.example.webbansach_backend.Enum.MessageType;
import com.example.webbansach_backend.Enum.RoleRoomChat;
import com.example.webbansach_backend.Enum.RoomType;
import com.example.webbansach_backend.Repository.MessageRepository;
import com.example.webbansach_backend.Repository.NguoiDungRepository;
import com.example.webbansach_backend.Repository.RoomNguoiDungRepository;
import com.example.webbansach_backend.Repository.RoomRepository;
import com.example.webbansach_backend.dto.Message.*;
import com.example.webbansach_backend.dto.NguoiDungChatResponeDTO;
import com.example.webbansach_backend.mapper.MessageMapper;
import com.example.webbansach_backend.mapper.NguoiDungMapper;
import com.example.webbansach_backend.service.ChatService;
import com.example.webbansach_backend.utils.ParseJacksonUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.sound.midi.Receiver;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

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
