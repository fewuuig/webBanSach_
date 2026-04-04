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
import com.example.webbansach_backend.dto.Message.MessageRequestDTO;
import com.example.webbansach_backend.dto.Message.MessageResponeDTO;
import com.example.webbansach_backend.mapper.MessageMapper;
import com.example.webbansach_backend.service.ChatMQVer2Service;
import com.example.webbansach_backend.utils.ParseJacksonUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Range;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Transactional
public class ChatMQVer2ServiceImpl implements ChatMQVer2Service {
    @Autowired
    @Qualifier("messageUserToUserMq")
    private DefaultRedisScript<Long> messageUserToUserMq ;
    @Autowired
    private RedisTemplate<String , MessageResponeDTO> redisTemplateJs2 ;
    @Autowired
    private MessageRepository messageRepository ;
    @Autowired
    private NguoiDungRepository nguoiDungRepository ;
    @Autowired
    private RoomRepository roomRepository ;
    @Autowired
    private RoomNguoiDungRepository roomNguoiDungRepository ;
    @Autowired
    private SimpMessagingTemplate messagingTemplate ;
    @Autowired
    private RedisTemplate<String , Object> redisTemplate ;
    @Autowired
    private MessageMapper messageMapper ;

    public void addMessageToStream(MessageRequestDTO messageRequestDTO){
        // push vào MQ (redis stream)
        String key = "chat-stream" ;
        String messageId = UUID.randomUUID().toString();
        Long result = redisTemplateJs2.execute(messageUserToUserMq ,
                List.of(key),
                messageRequestDTO.getSender(),
                messageRequestDTO.getContent(),
                messageRequestDTO.getSendToUser(),
                Instant.now() ,
                messageId
        );
        if(result !=1) throw new RuntimeException("send errol message!") ;
        // gửi luôn tin nhắn tại đây cho nhanh cx dc , nhưng phải cẩn thận
    }
    @PostConstruct
    private void initGroupChatStream(){
        try {
            redisTemplateJs2.opsForStream().createGroup("chat-dead-letter" , ReadOffset.latest(),"chat-dead-letter-group") ;
            redisTemplateJs2.opsForStream().createGroup("chat-stream" , ReadOffset.latest() , "chat-group") ;
        }catch (Exception ex){}
    }

    @Scheduled( fixedDelay=1000)
    @Transactional
    public void startConsumer(){
        List<MapRecord<String , Object ,Object>> messages =readMessage("chat-stream" ,
                Consumer.from("chat-group","consumer-1") ,
                500 ,
                Duration.ofSeconds(3));
        if((messages != null) && !messages.isEmpty())  {
            System.out.println("meesage : " + messages.size());
            System.out.println("vào tin nhắn consumer");
            processSaveBatch(messages) ;
        }
    }

    // ở đây có thẻ sử dung them set cuả redis để chống chùng
    public void processSaveBatch(List<MapRecord<String , Object , Object>> messages  ){
        // taọ danh sách lưu message đã sử lý
        if(messages.isEmpty()) return ;
        List<Message> messageBatch= new ArrayList<>() ;
        messages.forEach(record ->{
            Message mess = null;
            try {
                mess = buildMessage(record);
            } catch (InterruptedException e) {
                //next
            }
            messageBatch.add(mess) ;
        });

        messageRepository.saveAll(messageBatch) ;

        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        System.out.println("vào ack mes");
                        RecordId[] recordIds = messages.stream().map(mes->mes.getId()).toArray(RecordId[]::new) ;
                        messages.forEach(mess->{
                            sendMessage(mess);

                        });
                        redisTemplateJs2.opsForStream().acknowledge("chat-stream" , "chat-group" , recordIds) ;
                    }
                }
        );
    }
    public Message buildMessage(MapRecord<String , Object , Object> record) throws InterruptedException {
        //parse
        Instant timestamp = Instant.parse(ParseJacksonUtil.toString(record.getValue().get("timestamp").toString())) ;
        String content =  ParseJacksonUtil.toString(record.getValue().get("content").toString()) ;
        String sender = ParseJacksonUtil.toString(record.getValue().get("sender").toString()) ;
        String sendToUser = ParseJacksonUtil.toString(record.getValue().get("sendToUser").toString());

        // room
        NguoiDung senderNguoiDung = getNguoiDung(sender) ;
        NguoiDung reciever = getNguoiDung(sendToUser) ;
        Room room = getOrCreateRoom(senderNguoiDung , reciever) ;

        Message mess = new Message() ;
        mess.setRoom(room);
        mess.setNguoiDung(senderNguoiDung);
        mess.setContent(content);
        mess.setType(MessageType.TEXT);
        mess.setCreatedAt(timestamp);
        mess.setStatus(MessageStatus.SENDED);
        return mess ;
    }

    public Room getOrCreateRoom(NguoiDung user1 , NguoiDung user2) throws InterruptedException {
        int min = Math.min(user1.getMaNguoiDung() , user2.getMaNguoiDung()) ;
        int max = Math.min(user1.getMaNguoiDung() , user2.getMaNguoiDung()) ;
        // block : xem lại chỗ nayf

        Optional<Room> exists = roomRepository.findRoomUser(List.of(user1.getTenDangNhap(), user2.getTenDangNhap())) ;
        if(exists.isPresent()) return exists.get();

        // tạo room
        Room room = new Room() ;
        room.setType(RoomType.DM);
        room.setName("dm");
        roomRepository.save(room) ;

        // lưu roomNGuoiDung
        roomNguoiDungRepository.saveAll(List.of(createRoomUser(room , user1) , createRoomUser(room, user2))) ;
        return room ;


    }
    public RoomNguoiDung createRoomUser(Room room , NguoiDung user){
        RoomNguoiDung roomUser = new RoomNguoiDung() ;
        roomUser.setJoinedAt(LocalDateTime.now());
        roomUser.setRole(RoleRoomChat.MEMBER);
        roomUser.setRoom(room);
        roomUser.setNguoiDung(user);
        return roomUser ;
    }

    // cache room + block
    private final Map<String , NguoiDung> cacheUsers = new ConcurrentHashMap<>() ;
    public NguoiDung getNguoiDung(String nguoiDung){
        return cacheUsers.computeIfAbsent(nguoiDung , key->{
            return nguoiDungRepository.findByTenDangNhap(nguoiDung).orElseThrow(()->new RuntimeException("không thấy người dùng"));
        });
    }
    private void  sendMessage(MapRecord<String , Object , Object> message){
        // PARSE
        String sender = ParseJacksonUtil.toString(message.getValue().get("sender").toString()) ;
        String sendToUser = ParseJacksonUtil.toString(message.getValue().get("sendToUser").toString());
        String content = ParseJacksonUtil.toString(message.getValue().get("content").toString());
        Instant timestamp = Instant.parse(ParseJacksonUtil.toString(message.getValue().get("timestamp").toString())) ;

        // dto
        MessageResponeDTO messageResponeDTO = new MessageResponeDTO() ;
        messageResponeDTO.setContent(content);
        messageResponeDTO.setTimestamp(timestamp);
        messageResponeDTO.setSender(sender);
        System.out.println("đã gửi đến 2 client ");

        messagingTemplate.convertAndSendToUser(sender,"/queue/chat",messageResponeDTO);
        messagingTemplate.convertAndSendToUser(sendToUser,"/queue/chat",messageResponeDTO);
    }

    // retry message
    @Scheduled(fixedDelay = 5000)
    public void retryMessage(){
        PendingMessages pendings = redisTemplateJs2.opsForStream().pending(
                "chat-stream",
                Consumer.from("chat-group" , "consumer-1"),
                Range.unbounded(),
                500
        ) ;
        if(pendings.isEmpty()) return ;
        System.out.println("retry message");

        pendings.forEach(pending ->{
            if(pending.getTotalDeliveryCount() >=5){
               List<MapRecord<String , Object ,Object>> message=  claimMessage(pending);
               message.forEach(mess ->{
                   redisTemplateJs2.opsForStream().add(StreamRecords.mapBacked(mess.getValue()).withStreamKey("chat-dead-letter")) ;
               });
                redisTemplateJs2.opsForStream().acknowledge("chat-stream" , "chat-stream-group" , pending.getId()) ;
            }
            // tm dk sau thì processBatch
            if(pending.getElapsedTimeSinceLastDelivery().toSeconds() >=2){
                List<MapRecord<String , Object , Object>> message=claimMessage(pending);
                processSaveBatch(message);
            }
        });
    }

    // xưr lý dead-letter : coi như là tin nhắn lỗi k gửi đc
    @Scheduled(fixedDelay = 10000)
    public void retryMessageDeadLetter(){
        // only read àn set
        List<Message> listMessage = new ArrayList<>() ;
        List<MapRecord<String , Object , Object>> messages = readMessage("chat-dead-letter" ,
                                                                        Consumer.from("chat-dead-letter-group","consumer-1"),
                                                                        500,
                                                                        Duration.ofSeconds(2)) ;
        // xem lại
        processSaveBatch(messages);
    }
    // claim lại
    private List<MapRecord<String , Object , Object>> claimMessage(PendingMessage pending){
        return redisTemplateJs2.opsForStream().claim(
                "chat-stream",
                "chat-stream-group",
                "consumer-1",
                Duration.ofSeconds(3),
                pending.getId()
        );
    }
    // read message
    private List<MapRecord<String , Object , Object>> readMessage( String key,Consumer consumer , int count , Duration duration){
         return redisTemplateJs2.opsForStream().read(
                consumer,
                StreamReadOptions.empty().count(count).block(duration),
                StreamOffset.create(key , ReadOffset.lastConsumed())
        ) ;
    }

    @Override
    public List<MessageResponeDTO> getMessageOfUser(String sender , String sendToUser , int page , int size){
        // mỗi lần lấy 20 tin nhắn
        int start = -(page * size) ;
        int end =- (page*size) + size -1 ;

        // check xem user có trong phong chat khong
        Optional<Room> exists = roomRepository.findRoomUser(List.of(sender , sendToUser)) ;
        if(exists.isEmpty()) throw new RuntimeException("not found room chat of user:"+"["+sendToUser+","+sender+"]") ;

//        // lấy tin nhắn lên từ cache
//        // key :  chat:room:{idRoom}
//        String key = "chat:room:"+exists.get().getRoomId() ;
//       if(page == 1){
//           List<MessageResponeDTO> listMessage = redisTemplate2.opsForList().range(key , start ,end );
//
//           if(listMessage != null && !listMessage.isEmpty() && listMessage.size() == size) {
//               System.out.println("lấy tin nhắn từ cache");
//               return listMessage ;
//           }  ;
//       }

        // lâý hết tin nhắn của lên
        Page<Message> messages = messageRepository.findByRoom_RoomId(exists.get().getRoomId() ,
                PageRequest.of(page -1 , size , Sort.by("createdAt").descending())) ;

        // convert message
        List<MessageResponeDTO> messageResponeDTO = new ArrayList<>() ;
        messages.getContent().forEach(message ->{
            messageResponeDTO.add(messageMapper.toDTO((Message) message)) ;
        });
//        Collections.reverse(messageResponeDTO);
        // push vào cache khi miss
//        if(page ==1 ){
//            try {
//                redisTemplate2.delete(key) ;
//                messageResponeDTO.forEach(message ->{
//                    redisTemplate2.opsForList().rightPush(key , message) ;
//                });
//                redisTemplate.opsForList().trim(key , -200 , -1);
//            }catch (Exception ex){
//                System.out.println(ex.getMessage());
//            }
//        }

        System.out.println("request tinn hắn cũ");


        return messageResponeDTO ;
    }

}
