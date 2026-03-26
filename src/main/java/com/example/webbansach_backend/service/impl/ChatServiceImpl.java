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
    private SimpMessagingTemplate messagingTemplate ;
    @Autowired
//    @Qualifier("stringRedisTemplate")
    private RedisTemplate<String , Object> redisTemplate;
    @Autowired
    private RoomNguoiDungRepository roomNguoiDungRepository ;
    @Autowired
    @Qualifier("messageUserToUserMq")
    private DefaultRedisScript<Long> messageUserToUserMq ;
    @Autowired
    private MessageRepository messageRepository ;
    @Autowired
    private NguoiDungRepository nguoiDungRepository ;
    @Autowired
    private RoomRepository romRoomRepository ;
    @Autowired
    private MessageMapper messageMapper ;
    @Autowired
    private NguoiDungMapper nguoiDungMapper ;
    @Autowired
    private RoomRepository roomRepository ;
    @Autowired
    @Qualifier("redisOperations")
    private RedisTemplate<String , MessageResponeDTO> redisTemplate2 ;
//    @Override
//    @Transactional
//    public void insertDBAndSend(MessageRequestDTO message){
//
//        String sendToUser =message.getSendToUser() ;
//        String sender =message.getSender() ;
//        String content = message.getContent() ;
//
//        // kiểm tra xem cả hai đã có phòng chat cho lần đầu chưa
//        Optional<Room> exists = roomRepository.findRoomUser(List.of(sender , sendToUser)) ;
//        if(!exists.isPresent()){
//            // tạo phongf chat
//            Room room = new Room() ;
//            room.setType(RoomType.DM);
//            room.setName("dm");
//
//            // khởi tạo đối tượng
//            RoomNguoiDung senderEntity = new RoomNguoiDung() ;
//            RoomNguoiDung sendToUserEntity = new RoomNguoiDung() ;
//
//            // lấy người dùng lên
//            NguoiDung senderNguoiDung = nguoiDungRepository.findByTenDangNhap(sender)
//                    .orElseThrow(()->new RuntimeException("Không thấy user:"+sender)) ;
//            NguoiDung sendToUserNguoiDung = nguoiDungRepository.findByTenDangNhap(sendToUser)
//                    .orElseThrow(()->new RuntimeException("Không thấy user:"+sendToUser)) ;
//
//            // tạo phòng chat cho người gửi
//            senderEntity.setNguoiDung(senderNguoiDung);
//            senderEntity.setRole(RoleRoomChat.MEMBER);
//            senderEntity.setJoinedAt(LocalDateTime.now());
//            senderEntity.setRoom(room);
//
//            // tạo phòng chat cho người nhận
//            sendToUserEntity.setNguoiDung(sendToUserNguoiDung);
//            sendToUserEntity.setRole(RoleRoomChat.MEMBER);
//            sendToUserEntity.setJoinedAt(LocalDateTime.now());
//            sendToUserEntity.setRoom(room);
//
//            // tạo tin nhắn
//            Message mess = new Message() ;
//            mess.setRoom(room);
//            mess.setNguoiDung(senderNguoiDung);
//            mess.setContent(content);
//            mess.setType(MessageType.TEXT);
//            mess.setCreatedAt(Instant.now());
//            mess.setStatus(MessageStatus.SENDED);
//
//            // lưu DB
//            romRoomRepository.save(room) ;
//            roomNguoiDungRepository.saveAll(List.of(senderEntity , sendToUserEntity)) ;
//            messageRepository.save(mess) ;
//            return ;
//        }
//
//
//        NguoiDung nguoiDung = nguoiDungRepository.findByTenDangNhap(sender)
//                .orElseThrow(()->new RuntimeException("không thấy user:"+sender)) ;
//        Message mess = new Message() ;
//        mess.setRoom(exists.get());
//        mess.setNguoiDung(nguoiDung);
//        mess.setContent(content);
//        mess.setType(MessageType.TEXT);
//        mess.setCreatedAt(Instant.now());
//        mess.setStatus(MessageStatus.SENDED);
//        messageRepository.save(mess) ;
//        TransactionSynchronizationManager.registerSynchronization(
//                new TransactionSynchronization() {
//                    @Override
//                    public void afterCommit() {
//                        // gửi tin nhắn xong push vào lish luôn
////                        redisTemplate2.opsForList().rightPush("chat:room:"+exists.get().getRoomId() ,messageMapper.toDTO(mess) ) ;
//                        messagingTemplate.convertAndSendToUser(message.getSendToUser() , "/queue/chat" ,message);
//                        messagingTemplate.convertAndSendToUser(message.getSender() , "/queue/chat" ,message);
//
//                        System.out.println("gủi tin nhắn:"+content);
//                    }
//                }
//        );
//    }
//    @Override
//    public List<MessageResponeDTO> getMessageOfUser(String sender , String sendToUser , int page , int size){
//        // mỗi lần lấy 20 tin nhắn
//        int start = -(page * size) ;
//        int end =- (page*size) + size -1 ;
//
//        // check xem user có trong phong chat khong
//        Optional<Room> exists = roomRepository.findRoomUser(List.of(sender , sendToUser)) ;
//        if(exists.isEmpty()) throw new RuntimeException("not found room chat of user:"+"["+sendToUser+","+sender+"]") ;
//
////        // lấy tin nhắn lên từ cache
////        // key :  chat:room:{idRoom}
////        String key = "chat:room:"+exists.get().getRoomId() ;
////       if(page == 1){
////           List<MessageResponeDTO> listMessage = redisTemplate2.opsForList().range(key , start ,end );
////
////           if(listMessage != null && !listMessage.isEmpty() && listMessage.size() == size) {
////               System.out.println("lấy tin nhắn từ cache");
////               return listMessage ;
////           }  ;
////       }
//
//        // lâý hết tin nhắn của lên
//        Page<Message> messages = messageRepository.findByRoom_RoomId(exists.get().getRoomId() ,
//                                                   PageRequest.of(page -1 , size , Sort.by("createdAt").descending())) ;
//
//        // convert message
//        List<MessageResponeDTO> messageResponeDTO = new ArrayList<>() ;
//        messages.getContent().forEach(message ->{
//            messageResponeDTO.add(messageMapper.toDTO((Message) message)) ;
//        });
////        Collections.reverse(messageResponeDTO);
//        // push vào cache khi miss
////        if(page ==1 ){
////            try {
////                redisTemplate2.delete(key) ;
////                messageResponeDTO.forEach(message ->{
////                    redisTemplate2.opsForList().rightPush(key , message) ;
////                });
////                redisTemplate.opsForList().trim(key , -200 , -1);
////            }catch (Exception ex){
////                System.out.println(ex.getMessage());
////            }
////        }
//
//        System.out.println("request tinn hắn cũ");
//
//
//        return messageResponeDTO ;
//    }

//    @Override
//    public Set<String> getUsernameOfManager() {
//        return null;
//    }

    @Override
    public List<NguoiDungChatResponeDTO> getAllUser() {
        return null;
    }

    public List<NguoiDungChatResponeDTO> getUserListChat(){
        List<NguoiDung> nguoiDungs = nguoiDungRepository.findAll() ;
        return nguoiDungs.stream().map(nguoiDungMapper::toDTO).toList() ;
    }

}
