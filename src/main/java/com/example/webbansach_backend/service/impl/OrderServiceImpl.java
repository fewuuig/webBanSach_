package com.example.webbansach_backend.service.impl;

import com.example.webbansach_backend.Entity.*;
import com.example.webbansach_backend.Enum.LoaiMaGiamGia;
import com.example.webbansach_backend.Enum.StatusLogOrder;
import com.example.webbansach_backend.Enum.TrangThaiGiaoHang;
import com.example.webbansach_backend.Enum.TrangThaiMaGiamGia;
import com.example.webbansach_backend.Repository.*;
import com.example.webbansach_backend.dto.*;
import com.example.webbansach_backend.dto.OrderItem;
import com.example.webbansach_backend.exception.OutOfStockException;
import com.example.webbansach_backend.exception.VoucherStateException;
import com.example.webbansach_backend.mapper.DonHangMapper;
import com.example.webbansach_backend.service.*;
import com.example.webbansach_backend.utils.CheckRoleUItil;
import com.example.webbansach_backend.utils.ParseJacksonUtil;
import com.example.webbansach_backend.utils.TimeLogUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.transaction.Transactional;
import jdk.jfr.Recording;
import lombok.extern.java.Log;
import org.modelmapper.ModelMapper;
import org.modelmapper.internal.bytebuddy.implementation.bytecode.Throw;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import reactor.core.publisher.Flux;

import java.awt.*;
import java.lang.Record;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;


@Service

public class OrderServiceImpl implements OrderService {
    @Autowired
    private DonHangRepository donHangRepository ;
    @Autowired
    private SachRepository sachRepository ;
    @Autowired
    private NguoiDungRepository nguoiDungRepository ;
    @Autowired
    private HinhThucThanhToanRepository hinhThucThanhToanRepository ;
    @Autowired
    private HinhThucGiaoHangRepository hinhThucGiaoHangRepository ;
    @Autowired
    private DiaChiGiaoHangRepository diaChiGiaoHangRepository ;
    @Autowired
    private ModelMapper modelMapper ;
    @Autowired
    private RedisTemplate<String , Object> redisTemplate ;
    @Autowired
    @Qualifier("stockOrder")
    private DefaultRedisScript<Long> stockOrder ;
    @Autowired
    private LogOrderRepository logOrderRepository ;
    @Autowired
    private MaGiamGiaService maGiamGiaService ;
    @Autowired
    private ThongKeBanHangService thongKeBanHangService ;
    @Autowired
    private ReturnOrderTimeoutBatchService returnOrderTimeoutBatchService ;
    @Autowired
    private ObjectMapper objectMapper ;
    @Autowired
    private HinhThucGiaoHangCacheService hinhThucGiaoHangCacheService ;
    @Autowired
    private HinhThucThanhToanCacheService hinhThucThanhToanCacheService ;
    @Autowired
    private DiaChiGiaoHangCacheService diaChiGiaoHangCacheService ;

    // atomic
    @Override
    @Transactional
    public void placeOder( String tenDangNhap,DatHangRequestDTO datHangRequestDTO) throws JsonProcessingException {
        // sẽ két hợp với việc chjoongs spam API đối vs 1 user để k bị đặt hàng chùng - > trải nghiêm tệ đối với user
        // keys

        String requestId = UUID.randomUUID().toString() ;
        String itemJson = objectMapper.writeValueAsString(datHangRequestDTO.getItems()) ;
        String key1 = "order-stream" ;

        List<String> keys = Arrays.asList( key1 ) ;
//        String request_id = UUID.randomUUID().toString() ;
        // sử lý đătj hàng mà không dùng mã giảm giá
        String maGiam = datHangRequestDTO.getMaGiam() == null ? "null": datHangRequestDTO.getMaGiam().toString() ;
        Long result = redisTemplate.execute(stockOrder ,keys ,
                requestId,
                itemJson,
                maGiam,
                datHangRequestDTO.getMaDiaChiGiaoHang(),
                datHangRequestDTO.getMaHinhThucThanhToan() ,
                datHangRequestDTO.getMaHinhThucGiaoHang(),
                tenDangNhap
        );
        if(result == -1) throw  new RuntimeException("kho không tồn tại") ;
        if(result == -2) throw  new RuntimeException("kho bị âm") ;
        if(result == -4) throw  new RuntimeException("số lượng muốn mua phải là chữu số") ;
        if(result == -3) throw  new RuntimeException("số lượng muốn mua phải laf số dương (klhoong được âm)") ;
        if(result ==  0) throw  new RuntimeException("kho không đủ") ;
        if(result == -5) throw  new RuntimeException(tenDangNhap + " Spam API quá số lần quy định") ;
        if(result == -9) throw new RuntimeException("số lượng mua không hợp lệ") ;
        System.out.println("["+TimeLogUtil.toTimeSystemLog() +"]" + " user:"+tenDangNhap+"đặt đơn hàng");

//        // đặt thanhf công thì xóa khỏi giỏ hàng
//        Set<Long> ids = datHangRequestDTO.getDanhSachSanPhamChon() ;
//        cartService.deleteItemOrderFromCart(tenDangNhap , ids);

    }
    // tính năng của admin
    @Override
    @Transactional
    public void capNhatTrangThaiDonHang( String tenDangNhap,int maDonHang, TrangThaiGiaoHang trangThai){
        NguoiDung nguoiDung= nguoiDungRepository.findByTenDangNhap(tenDangNhap).
                orElseThrow(()->new RuntimeException("NGuoi dung không tồn tại")) ;
        if(!CheckRoleUItil.checkRoleAdminOrUser(nguoiDung)) throw new RuntimeException("Người dùng khôpng đủ quyền để cập nhật đơn hàng") ;
        DonHang donHang = donHangRepository.findById(maDonHang).orElseThrow() ;
        donHang.setTrangThai(trangThai) ;
    }

    // xem lại chỗ này N + 1 : chi tiết đơn hàng - đơn hànggg
    // kiẻm trA inddex xem đã đúng chưa . xem còn cách khác khong .
    @Override
    @Transactional
    public List<DonHangTrangThaiResponeDTO> getDonHangTheoTrangThai(String tenDangNhap , TrangThaiGiaoHang trangThaiGiaoHang) {
        NguoiDung nguoiDung = nguoiDungRepository.findByTenDangNhap(tenDangNhap).orElseThrow() ;
        List<DonHang> donHangs = donHangRepository.findByNguoiDungAndTrangThai( nguoiDung, trangThaiGiaoHang) ;
        if(donHangs.isEmpty() ) return null ;
        List<DonHangTrangThaiResponeDTO> result = new ArrayList<>() ;
        for(DonHang donHang : donHangs){
            DonHangTrangThaiResponeDTO donHangTrangThaiResponeDTO = modelMapper.map(donHang, DonHangTrangThaiResponeDTO.class) ;
            donHangTrangThaiResponeDTO.setHoTen(nguoiDung.getHoDem() + nguoiDung.getTen());
            donHangTrangThaiResponeDTO.setSoDienThoai(nguoiDung.getSoDienThoai());
            List<SachTrongDonDTO> sachTrongDonDTO = new ArrayList<>() ;
            for(ChiTietDonHang chiTietDonHang : donHang.getDanhSachChiTietDonHang()){
                SachTrongDonDTO sachTrongDon = modelMapper.map(chiTietDonHang,SachTrongDonDTO.class) ;
                sachTrongDon.setTenSach(chiTietDonHang.getSach().getTenSach());
                sachTrongDon.setMaSach(chiTietDonHang.getSach().getMaSach());
                sachTrongDonDTO.add(sachTrongDon) ;
            }
            donHangTrangThaiResponeDTO.setSachTrongDonDTOS(sachTrongDonDTO);
            result.add(donHangTrangThaiResponeDTO) ;

        }
        return result ;
    }
    @Override
    @Transactional
    // multithread
    public void thaoTacDonHang(String tenDangNhap , int maDonHang){
        int totalBook = 0 ;
        DonHang donHang = donHangRepository.findByNguoiDung_TenDangNhapAndMaDonHang(tenDangNhap , maDonHang).
                orElseThrow(()->new RuntimeException("Don hàng không tồn tại")) ;
        if(donHang.getTrangThai().equals(TrangThaiGiaoHang.DA_HUY)){
            for(ChiTietDonHang chiTietDonHang : donHang.getDanhSachChiTietDonHang()){
                // permistic_lock
                Sach sach = sachRepository.findByIdForUpdate(chiTietDonHang.getSach().getMaSach()).orElseThrow() ;
                if(sach.getSoLuong() >= chiTietDonHang.getSoLuong() && sach.isActive()){
                    totalBook += chiTietDonHang.getSoLuong() ;
                    sach.setSoLuong(sach.getSoLuong() - chiTietDonHang.getSoLuong());
                }else {
                    throw new RuntimeException("Số lượng sachs khòng đủ để đặt hàng") ;
                }
            }
            System.out.println("["+ TimeLogUtil.toTimeSystemLog() +"]"+" user:" + tenDangNhap +":đặt lại đơn hàng:"+maDonHang);
            donHang.setTrangThai(TrangThaiGiaoHang.DA_XAC_NHAN);
        }else if(donHang.getTrangThai().equals(TrangThaiGiaoHang.CHO_XAC_NHAN)){
            List<ChiTietDonHang> chiTietDonHangs = donHang.getDanhSachChiTietDonHang() ;
            for(ChiTietDonHang chiTietDonHang : chiTietDonHangs){
                // lock
                Sach sach = sachRepository.findByIdForUpdate(chiTietDonHang.getSach().getMaSach()).orElseThrow() ;
                sach.setSoLuong(sach.getSoLuong() + chiTietDonHang.getSoLuong());
            }
            System.out.println("["+ TimeLogUtil.toTimeSystemLog() +"]" + " user:"+tenDangNhap+":hủy đơn:"+maDonHang);
            donHang.setTrangThai(TrangThaiGiaoHang.DA_HUY);
        }else {
            throw new RuntimeException("khong thể thao tác với trạng thái này , thao tác không hợp lệ") ;
        }
        int finalTotalBook = totalBook;
        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        thongKeBanHangService.statWhenPlaceOrder(finalTotalBook,donHang);
                    }
                }
        );

    }
    @PostConstruct
    public void initOrderStream(){
        try {
            redisTemplate.opsForStream().createGroup("order-stream" , ReadOffset.latest() , "order-group") ;
            redisTemplate.opsForStream().createGroup("order-dead-letter" , ReadOffset.latest() , "order-dead-letter-group") ;
        }catch (Exception ignoreException){
            // có rồi thì thôi .
        }
    }
    @Transactional
    @Scheduled(fixedDelay = 1000)  //
    public void consumeNewMessage() throws JsonProcessingException {
        List<MapRecord<String , Object , Object>> messages = redisTemplate.opsForStream().read(
                Consumer.from("order-group" , "consumer-1") ,
                StreamReadOptions.empty().count(300).block(Duration.ofSeconds(2)) ,
                StreamOffset.create("order-stream" , ReadOffset.lastConsumed())
        ) ;

        if(messages.isEmpty() || messages == null) return ;

        // xử lý đơn hàng theo lô
        saveBatch(messages);
    }
    @Scheduled(fixedDelay = 30000)
    @Transactional
    public void retryOrder() throws JsonProcessingException {

        PendingMessagesSummary pendingMessagesSummary = redisTemplate.opsForStream().pending(
                "order-stream" ,
                "order-group"
        ) ;
        if(pendingMessagesSummary == null || pendingMessagesSummary.getTotalPendingMessages() == 0) return ;

        PendingMessages pendingMessages = redisTemplate.opsForStream().pending(
                "order-stream" ,
                Consumer.from("order-group" , "consumer-1") ,
                Range.unbounded() , 300
        ) ;

        // tạo 1 list message để xử lý theo lô
        List<MapRecord<String , Object ,Object>> messages = new ArrayList<>( );
        for(PendingMessage pendingMessage : pendingMessages){
            if(pendingMessage.getTotalDeliveryCount() >=6){
                List<MapRecord<String , Object,Object>> claimed  = redisTemplate.opsForStream().claim(
                        "order-stream" ,
                        "order-group" ,
                        "consumer-1" ,
                        Duration.ofSeconds(30) ,
                        pendingMessage.getId()
                );
                // lưu vào stream dead-letter để sưe lý sau
                for(MapRecord<String , Object ,Object> message : claimed){
                    redisTemplate.opsForStream()
                            .add(StreamRecords.mapBacked(message.getValue()).withStreamKey("order-dead-letter")) ;
                }
                // ACK để xóa nó khỏi PEL của voucher-stream -> voucher-group
                redisTemplate.opsForStream().acknowledge(
                        "order-stream",
                        "order-group" ,
                        pendingMessage.getId()
                );
                continue;
            }

            if(pendingMessage.getElapsedTimeSinceLastDelivery().toSeconds() >= 30){
                List<MapRecord<String , Object,Object>> claimed  = redisTemplate.opsForStream().claim(
                        "order-stream" ,
                        "order-group" ,
                        "consumer-1" ,
                        Duration.ofSeconds(2) ,
                        pendingMessage.getId()
                );
               // add message ở đây
                for (MapRecord<String ,Object ,Object> message : claimed){
                    messages.add(message) ;
                }
            }
        }
        if(messages.isEmpty()) return ;
        saveBatch(messages);
    }

    public void saveBatch(List<MapRecord<String , Object , Object>> messages) throws JsonProcessingException {
        // gom id sách  va tenDangNhap
        Set<String> tenDangNhaps = new HashSet<>() ;
        Set<Integer> idSaches = new HashSet<>() ;
        for(MapRecord<String , Object , Object> message: messages){
            tenDangNhaps.add(ParseJacksonUtil.toString(message.getValue().get("tenDangNhap").toString())) ;
            String itemJson = ParseJacksonUtil.toString(message.getValue().get("items").toString());
            List<OrderItem> items = objectMapper.readValue(itemJson, new TypeReference<List<OrderItem>>() {});
            for(OrderItem item : items){
                idSaches.add(item.getMaSach()) ;
            }
        }
        // batch sách
        Map<Integer , Sach> sachMap = sachRepository.findByMaSachInAndIsActive(idSaches , true).
                stream().collect(Collectors.toMap(Sach::getMaSach,s->s)) ;
        // batch nguoiDung
        Map<String , NguoiDung> nguoiDungMap = nguoiDungRepository.findByTenDangNhapIn(tenDangNhaps)
                .stream().collect(Collectors.toMap(NguoiDung::getTenDangNhap, s->s)) ;


        //List Log & List Don hang
        List<LogOrder> logs = new ArrayList<>() ;
        List<DonHang> donHangs = new ArrayList<>() ;

        // action aftercommit
        List<Map<String ,Object >> actionAfterCommit = new ArrayList<>() ;

        for(MapRecord<String,Object,Object> message : messages){
            String request_Id = ParseJacksonUtil.toString(message.getValue().get("request_id").toString()) ;
            String maGiam = ParseJacksonUtil.toString(message.getValue().get("maGiam").toString()) ;
            if(isLogOrder(message ,request_Id)) continue;
            // create
            DonHang donHang = makeDonHang(message, maGiam , sachMap , nguoiDungMap);
            LogOrder log =makeLogOrder(message ,request_Id ,maGiam) ;
            // add list
            logs.add(log) ;
            donHangs.add(donHang) ;

            // add field need to act after commit
            actionAfterCommit.add(Map.of(
                    "maGiam" , maGiam,
                    "tenDangNhap" ,ParseJacksonUtil.toString(message.getValue().get("tenDangNhap").toString()),
                    "donHang" , donHang,
                    "request_id" , request_Id ,
                    "messageId" , message.getId()
            ));
        }

        logOrderRepository.saveAll(logs) ;
        donHangRepository.saveAll(donHangs) ;

        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {

                        // java k cho tạo new Arr[] rỗng
                        List<RecordId> recordIds = new ArrayList<>() ;

                        for (Map<String , Object> action : actionAfterCommit  ) {
                            String maGiam = (String) action.get("maGiam");
                            String tenDangNhap = (String) action.get("tenDangNhap");
                            DonHang donHang = (DonHang) action.get("donHang");
                            int maDonHang = donHang.getMaDonHang();
                            String request_Id = (String) action.get("request_id");
                            recordIds.add((RecordId) action.get("messageId"));
                            if (!maGiam.equals("null")) {
                                // push vào queue delay -> hoàn kho nếu k thể xác nhận đơn hang
                                returnOrderTimeoutBatchService.addOrderTimeout(maDonHang);
                                int value = Integer.parseInt(maGiam);
                                maGiamGiaService.dungMaGiamGiaUser(tenDangNhap, value, request_Id);

                                System.out.println("[" + TimeLogUtil.toTimeSystemLog() + "]" + " user:" + tenDangNhap + ":chờ xử lý mã giảm giá");
                            }
                            if (maGiam.equals("null"))
                                System.out.println("[" + TimeLogUtil.toTimeSystemLog() + "]" + " user:" + tenDangNhap + ":đặt đơn hàng thành công");
                        }
                        thongKeBanHangService.onStatsToday();
                        redisTemplate.opsForStream().acknowledge("order-stream" ,"order-group" ,recordIds.toArray(new RecordId[0]) ) ;

                    }
                }
        );
    }
    @Transactional
    @Scheduled(fixedDelay = 5000) // 5s bù kho một lần
    public void compensateStockRedisBook() throws JsonProcessingException {
        List<MapRecord<String, Object, Object>> messages = redisTemplate.opsForStream().read(
                Consumer.from("order-dead-letter-group", "consumer-1"),
                StreamReadOptions.empty().count(700).block(Duration.ofSeconds(2)),
                StreamOffset.create("order-dead-letter", ReadOffset.lastConsumed())
        );
        if (messages == null || messages.isEmpty()) return;
        System.out.println("bù kho order") ;

        // lấy ra danh sách id voucher để check dưới DB rồi reconcicle
        Set<Integer> idBooks = new HashSet<>() ;
        for (MapRecord<String, Object, Object> message : messages) {
            String itemString = ParseJacksonUtil.toString(message.getValue().get("items").toString()) ;
            List<OrderItem> items = objectMapper.readValue(itemString, new TypeReference<List<OrderItem>>() {}) ;
            for(OrderItem item : items){
                idBooks.add(item.getMaSach());
            }
        }
        List<Sach> saches = sachRepository.findByMaSachInAndIsActive(idBooks,true) ;
        if(saches == null) return ;

        for(Sach sach : saches ){
            Integer value = null ;
            try {
                 value = Integer.parseInt(redisTemplate.opsForValue().get("book:"+sach.getMaSach()).toString()) ;
            }catch (NumberFormatException ex){
                redisTemplate.opsForValue().set("book:"+sach.getMaSach() , sach.getSoLuong());
                continue;
            }
            if(sach.getSoLuong() == value) continue;
            else redisTemplate.opsForValue().set("book:"+sach.getMaSach() , sach.getSoLuong());
            System.out.println("đã bù kho redis mã sach : " + sach.getMaSach());
        }

        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {

//                        RecordId[] recordIds = messages.stream().map(message->message.getId()).toArray(RecordId[]::new);
                        messages.forEach(mess->{
                            redisTemplate.opsForStream().acknowledge("order-dead-letter", "order-dead-letter-group",mess.getId());
                        });

                    }

                }
        );

    }

    private void discount(String maGiam , DonHang donHang , double totalPrice){
        if(maGiam.equals("null")){
            donHang.setTongGia(totalPrice);
        }else {
            MaGiamGia maGiamGia =maGiamGiaService.getMaGiamGia(Integer.parseInt(maGiam)) ;
            if(maGiamGia.getTrangThaiMaGiamGia() == TrangThaiMaGiamGia.KHOA || maGiamGia.getTrangThaiMaGiamGia() == TrangThaiMaGiamGia.HET_HAN){
                throw new VoucherStateException("mã giảm giá không còn hoạt động") ;
            }
            donHang.setMaGiamGia(maGiamGia);
            if(maGiamGia.getLoaiMaGiamGia() == LoaiMaGiamGia.PHAN_TRAM){
                if(totalPrice >= maGiamGia.getDonGiaTu()){
                    double tienGiam =(totalPrice * maGiamGia.getPhanTramGiam())/100 ;
                    if(tienGiam <= maGiamGia.getGiamToiDa()) donHang.setTongGia(totalPrice - tienGiam );
                    else if(tienGiam > maGiamGia.getGiamToiDa()) donHang.setTongGia(totalPrice - maGiamGia.getGiamToiDa());
                }else donHang.setTongGia(totalPrice);
            }else if(maGiamGia.getLoaiMaGiamGia() == LoaiMaGiamGia.TIEN){
                double tienPhaiTra = totalPrice - maGiamGia.getTienGiam() ;
                if(totalPrice >= maGiamGia.getDonGiaTu()) donHang.setTongGia( tienPhaiTra < 0 ? 0 : tienPhaiTra );
                else if(totalPrice < maGiamGia.getDonGiaTu()) donHang.setTongGia(totalPrice);
            }
        }
        // tổng với tiền sip
        donHang.setTongGia(donHang.getTongGia() + donHang.getChiPhiGiaoHang());
    }
    private DonHang detailOfDonHang(MapRecord<String , Object ,Object> message ,NguoiDung nguoiDung){
        String request_Id = ParseJacksonUtil.toString(message.getValue().get("request_id").toString()) ;
        // tạo dơn hàng
        // chỗ này sử lý theo ram

        DonHang donHang = new DonHang() ;
        HinhThucThanhToan hinhThucThanhToan = hinhThucThanhToanRepository.findById(Integer.parseInt(message.getValue().get("maHinhThucThanhToan").toString()))
                        .orElseThrow(()->new RuntimeException("Không thấy HÌnh thức thamh toán")) ;
        HinhThucGiaoHang hinhThucGiaoHang = hinhThucGiaoHangRepository.
                findById(Integer.parseInt(message.getValue().get("maHinhThucGiaoHang").toString())).
                orElseThrow(()->new RuntimeException("Không thấy HÌnh thức giao hàng")) ;
        DiaChiGiaoHang diaChiGiaoHang = diaChiGiaoHangRepository.
                findById(Integer.parseInt(message.getValue().get("maDiaChiGiaoHang").toString())).
                orElseThrow(()->new RuntimeException("Không thấy địa chỉ giao hàng"));
        String diaChi = diaChiGiaoHang.getSoNha() +","+ diaChiGiaoHang.getPhuongOrXa() +","+ diaChiGiaoHang.getQuanOrHuyen() +","+ diaChiGiaoHang.getTinhOrCity() ;
        donHang.setNguoiDung(nguoiDung);
        donHang.setNgayTao(LocalDateTime.now());

        if(hinhThucGiaoHang.getTenHinhThucGiaoHang().equals("Giao hàng nhanh")
                || hinhThucGiaoHang.getTenHinhThucGiaoHang().equals("Giao hàng tiết kiệm")
                || hinhThucGiaoHang.getTenHinhThucGiaoHang().equals("Hỏa tốc") )
        {
            donHang.setChiPhiGiaoHang(hinhThucGiaoHang.getChiPhiGiaoHang());
        }
        donHang.setTrangThai(TrangThaiGiaoHang.CHO_XAC_NHAN); // default = "CHO_XAC_NHAN"
        donHang.setHinhThucThanhToan(hinhThucThanhToan);
        donHang.setHinhThucGiaoHang(hinhThucGiaoHang);
        donHang.setDiaChiNhanHang(diaChi);
        donHang.setRequestId(request_Id);

        return donHang ;
    }
    private ChiTietDonHang makeChiTietDonHang(DonHang donHang , Sach sach , OrderItem item){
        ChiTietDonHang chiTietDonHang = new ChiTietDonHang() ;
        chiTietDonHang.setDonHang(donHang);
        chiTietDonHang.setSach(sach);
        chiTietDonHang.setSoLuong(item.getSoLuong());
        chiTietDonHang.setGiaBan(sach.getGiaBan()) ;
        chiTietDonHang.setTongGia(sach.getGiaBan() * item.getSoLuong());
        return chiTietDonHang ;
    }
    private DonHang makeDonHang(MapRecord<String , Object ,Object> message , String maGiam ,Map<Integer ,Sach>  sachMap ,Map<String , NguoiDung> nguoiDungMap) throws JsonProcessingException {
        // Kiểm tra người dùng từ batch
        String tenDangNhap = ParseJacksonUtil.toString(message.getValue().get("tenDangNhap").toString()) ;
        NguoiDung nguoiDung = nguoiDungMap.get(tenDangNhap) ;
        if(nguoiDung == null) throw new RuntimeException("Nguoi dùng không tônf tại") ;
        // parse json -> object List
        String iteamJson = ParseJacksonUtil.toString(message.getValue().get("items").toString()) ;
        List<OrderItem>  items= objectMapper.readValue(iteamJson, new TypeReference<List<OrderItem>>(){});
        // tạo người dùng
        DonHang donHang = detailOfDonHang(message ,nguoiDung) ;
        // khai báo gía tiền của đơn
        double totalPrice = 0 ;
        int totalBook = 0 ;


        // tạo chi tiết đơn hang
        for(OrderItem item : items){
            if(item.getSoLuong() <= 0) throw new RuntimeException("số lượng muốn mua không hợp lệ") ;
            Sach sach = sachMap.get(item.getMaSach()) ;

            if(sach.getSoLuong() == 0 || sach.getSoLuong() < item.getSoLuong() || !sach.isActive()){
                throw new RuntimeException("số lượng sách trong kho không hợp lệ / số lượng muốn mua không hợp lệ/bi khoa") ;
            }
            // trừ DB
            sach.setSoLuong(sach.getSoLuong() - item.getSoLuong());
            // lên chi tiết cho dơn hàng
            ChiTietDonHang chiTietDonHang =makeChiTietDonHang(donHang,sach,item) ;
            donHang.getDanhSachChiTietDonHang().add(chiTietDonHang);
            // tổng số sách
            totalBook += item.getSoLuong() ;
            //tiền mua sách
            totalPrice = totalPrice + sach.getGiaBan()*item.getSoLuong() ;
            // thống kê bán chạy
            redisTemplate.opsForZSet().incrementScore("top:selling:books" , item.getMaSach() , item.getSoLuong());
            thongKeBanHangService.statWhenPlaceOrder(totalBook,donHang);
        }
        // giảm giá tiền : 1 method + tiền ship
        discount(maGiam,donHang,totalPrice) ;
        // nếu k mã giảm giá thì confirmd luôn
        if(maGiam.equals("null")){
            donHang.setTrangThai(TrangThaiGiaoHang.DA_XAC_NHAN);
        }

        // dừng tại đây tạo 1 đơn hàng là ok
        return donHang ;
    }
    private LogOrder makeLogOrder(MapRecord<String , Object ,Object> message  ,String request_Id ,String maGiam){
        LogOrder log = new LogOrder() ;
        log.setCreateAt(LocalDateTime.now());
        if(maGiam.equals("null")){
            log.setStatus(StatusLogOrder.CONFIRMED);
        }else {
            log.setStatus(StatusLogOrder.PENDING);
            log.setVoucherID(Integer.parseInt(ParseJacksonUtil.toString(message.getValue().get("maGiam").toString()))) ;
        }
        log.setRequestId(request_Id) ;
        return log ;
    }
    private boolean isLogOrder( MapRecord<String , Object ,Object> message,String request_Id){
        boolean exist = logOrderRepository.existsByRequestId(request_Id);
        if(exist){
            redisTemplate.opsForStream().acknowledge("order-stream" , "order-group" , message.getId()) ;
            return true ;
        }
        return false ;
    }
}


