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
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.internal.bytebuddy.implementation.bytecode.Throw;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
    private CartService cartService ;
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



    // atomic
    @Override
    @Transactional
    public void placeOder( String tenDangNhap,DatHangRequestDTO datHangRequestDTO) throws JsonProcessingException {
        // sẽ két hợp với việc chjoongs spam API đối vs 1 user để k bị đặt hàng chùng - > trải nghiêm tệ đối với user
        // keys
        ObjectMapper objectMapper = new ObjectMapper() ;
        String itemJson = objectMapper.writeValueAsString(datHangRequestDTO.getItems()) ;
        String key1 = "order-stream" ;
        String key2 = "limit_rate_request:" + tenDangNhap ;
        List<String> keys = Arrays.asList( key1 , key2) ;
        String request_id = UUID.randomUUID().toString() ;
        // sử lý đătj hàng mà không dùng mã giảm giá
        String maGiam = datHangRequestDTO.getMaGiam() == null ? "null": datHangRequestDTO.getMaGiam().toString() ;
        Long result = redisTemplate.execute(stockOrder ,keys ,
                request_id,
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

        // đặt thanhf công thì xóa khỏi giỏ hàng
        Set<Long> ids = datHangRequestDTO.getDanhSachSanPhamChon() ;
        cartService.deleteItemOrderFromCart(tenDangNhap , ids);

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
    @Override
    @Transactional
    public List<DonHangTrangThaiResponeDTO> getDonHangTheoTrangThai(String tenDangNhap , TrangThaiGiaoHang trangThaiGiaoHang) {
        NguoiDung nguoiDung = nguoiDungRepository.findByTenDangNhap(tenDangNhap).orElseThrow() ;
        List<DonHang> donHangs = donHangRepository.findByNguoiDungAndTrangThai( nguoiDung, trangThaiGiaoHang) ;
        if(donHangs == null ) return null ;
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
                    totalBook =+ chiTietDonHang.getSoLuong() ;
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
            redisTemplate.opsForStream().createGroup("order-dead-letter" , ReadOffset.latest() , "order-dead-letter-group") ;
            redisTemplate.opsForStream().createGroup("order-stream" , ReadOffset.latest() , "order-group") ;
        }catch (Exception ignoreException){
            // có rồi thì thôi .
        }
    }
    @Transactional
    @Scheduled(fixedDelay = 1000)  //
    public void consumeNewMessage() throws JsonProcessingException {
        List<MapRecord<String , Object , Object>> messages = redisTemplate.opsForStream().read(
                Consumer.from("order-group" , "consumer-1") ,
                StreamReadOptions.empty().count(10).block(Duration.ofSeconds(2)) ,
                StreamOffset.create("order-stream" , ReadOffset.lastConsumed())
        ) ;

        if(messages == null) return ;

        // xử lý đơn hàng ở đây
        for (MapRecord<String, Object, Object> message : messages) {
            handleMessage(message);
        }
    }
    @Scheduled(fixedDelay = 5000)
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
                Range.unbounded() , 10
        ) ;

        for(PendingMessage pendingMessage : pendingMessages){
            if(pendingMessage.getTotalDeliveryCount() >=5){
                List<MapRecord<String , Object,Object>> claimed  = redisTemplate.opsForStream().claim(
                        "order-stream" ,
                        "order-group" ,
                        "consumer-1" ,
                        Duration.ofSeconds(5) ,
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

            if(pendingMessage.getElapsedTimeSinceLastDelivery().toSeconds() >= 10){
                List<MapRecord<String , Object,Object>> claimed  = redisTemplate.opsForStream().claim(
                        "order-stream" ,
                        "order-group" ,
                        "consumer-1" ,
                        Duration.ofSeconds(10) ,
                        pendingMessage.getId()
                );

               // xử lý message ở đây
                for (MapRecord<String, Object, Object> mapRecord : claimed) {
                    handleMessage(mapRecord);
                }
            }

        }
    }
    public void handleMessage(MapRecord<String , Object , Object> message ) throws JsonProcessingException {
        boolean exist = logOrderRepository.existsByRequestId(ParseJacksonUtil.toString(message.getValue().get("request_id").toString()));
        // idempotent DB
        if(exist){
            redisTemplate.opsForStream().acknowledge("order-stream" , "order-group" , message.getId()) ;
            return ;
        }
        Integer maDonHang =  processOrder(message); // nó sẽ chạy chung 1 transition với handleMessage chứ k chạy cái transis=tion của riêng nó .

        LogOrder entity = new LogOrder() ;
        entity.setCreateAt(LocalDateTime.now());
        String  maGiam = ParseJacksonUtil.toString(message.getValue().get("maGiam").toString()) ;
        if(maGiam.equals("null")){  // trường hơp không dunmgf mã giảm giá
            entity.setStatus(StatusLogOrder.CONFIRMED);
        }else {
            entity.setStatus(StatusLogOrder.PENDING);
            entity.setVoucherID(Integer.parseInt(ParseJacksonUtil.toString(message.getValue().get("maGiam").toString()))) ;
        }
        entity.setRequestId(ParseJacksonUtil.toString(message.getValue().get("request_id").toString())) ;
        logOrderRepository.save(entity) ;

        // chỉ khi thành công mới ACK
        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        String maGiam = ParseJacksonUtil.toString(message.getValue().get("maGiam").toString()) ;
                        String tenDangNhap = ParseJacksonUtil.toString(message.getValue().get("tenDangNhap").toString()) ;
                        String request_id = ParseJacksonUtil.toString(message.getValue().get("request_id").toString()) ;
                        if(!maGiam.equals("null")) {
                            // push vào queue delay -> hoàn kho nếu k thể xác nhận đơn hang
                            returnOrderTimeoutBatchService.addOrderTimeout(maDonHang);
                            int value = Integer.parseInt(maGiam) ;
                            maGiamGiaService.dungMaGiamGiaUser(tenDangNhap , value ,request_id );

                            System.out.println("["+TimeLogUtil.toTimeSystemLog() +"]" + " user:"+tenDangNhap+":chờ xử lý mã giảm giá");
                        }
                        // gửi thống kê
                        thongKeBanHangService.onStatsToday();
                        //ACK
                        redisTemplate.opsForStream().acknowledge("order-stream" ,"order-group" , message.getId()) ;
                        if(maGiam.equals("null")) System.out.println("["+TimeLogUtil.toTimeSystemLog() +"]" + " user:"+tenDangNhap+":đặt đơn hàng thành công");
                    }
                }
        );

    }

    public Integer processOrder(MapRecord<String , Object , Object> message) throws JsonProcessingException {
        // Kiểm tra người dùng => choox nayf toi uu bang cach check tu redis nhu check ussername .
        NguoiDung nguoiDung = nguoiDungRepository.findByTenDangNhap(ParseJacksonUtil.toString(message.getValue().get("tenDangNhap").toString())).orElseThrow() ;

        // parse json -> object List
        String iteamJson = ParseJacksonUtil.toString(message.getValue().get("items").toString()) ;
        ObjectMapper objectMapper = new ObjectMapper() ;
        List<OrderItem>  items= objectMapper.readValue(iteamJson, new TypeReference<List<OrderItem>>(){});

        // tạo dơn hàng
        DonHang donHang = new DonHang() ;
        HinhThucThanhToan hinhThucThanhToan = hinhThucThanhToanRepository.
                findById(Integer.parseInt(message.getValue().get("maHinhThucThanhToan").toString())).
                orElseThrow(()->new RuntimeException("Không thấy HÌnh thức thamh toán")) ;
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
        donHang.setRequestId(ParseJacksonUtil.toString(message.getValue().get("request_id").toString()));
        // mã giảm giá
        String maGiam = ParseJacksonUtil.toString(message.getValue().get("maGiam").toString()) ;
        double totalPrice = 0 ;
        int totalBook = 0 ;
        // tạo chi tiết đơn hangf
        for(OrderItem item : items){
            if(item.getSoLuong() <= 0) throw new RuntimeException("số lượng muốn mua không hợp lệ") ;
            // permistic_lock
            Sach sach = sachRepository.findByIdForUpdate(item.getMaSach()).orElseThrow(()->new RuntimeException("sách không tông tại")) ;
            if(sach.getSoLuong() == 0 || sach.getSoLuong() < item.getSoLuong() || !sach.isActive()){
                throw new RuntimeException("số lượng sách trong kho không hợp lệ / số lượng muốn mua không hợp lệ") ;
            }
            // trừ DB
            sach.setSoLuong(sach.getSoLuong() - item.getSoLuong());
            // lên chi tiết cho dơn hàng
            ChiTietDonHang chiTietDonHang = new ChiTietDonHang() ;
            chiTietDonHang.setDonHang(donHang);
            chiTietDonHang.setSach(sach);
            chiTietDonHang.setSoLuong(item.getSoLuong());
            chiTietDonHang.setGiaBan(sach.getGiaBan()) ;
            chiTietDonHang.setTongGia(sach.getGiaBan() * item.getSoLuong());
            donHang.getDanhSachChiTietDonHang().add(chiTietDonHang);
            // tổng số sách
            totalBook += item.getSoLuong() ;
            //tiền mua sách
            totalPrice = totalPrice + sach.getGiaBan()*item.getSoLuong() ;
        }

        // giảm giá tiền
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
        // thống kê doanh số bán hàng
        thongKeBanHangService.statWhenPlaceOrder(totalBook ,donHang);

        // nếu k mã giảm giá thì confirmd luôn
        if(maGiam.equals("null")){
            donHang.setTrangThai(TrangThaiGiaoHang.DA_XAC_NHAN);
        }
        // luwu don hang
        donHangRepository.save(donHang) ;
        return donHang.getMaDonHang() ;
    }

    @Transactional
    @Scheduled(fixedDelay = 5000) // 5s bù kho một lần
    public void compensateStockRedisBook() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper() ;

        List<MapRecord<String, Object, Object>> messages = redisTemplate.opsForStream().read(
                Consumer.from("order-dead-letter-group", "consumer-1"),
                StreamReadOptions.empty().count(10).block(Duration.ofSeconds(2)),
                StreamOffset.create("order-dead-letter", ReadOffset.lastConsumed())
        );
        if (messages == null || messages.isEmpty()) return;

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
                        messages.forEach(message -> redisTemplate.opsForStream().acknowledge("order-dead-letter", "order-dead-letter-group", message.getId()));
                    }

                }
        );

    }

}


