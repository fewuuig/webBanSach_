package com.example.webbansach_backend.service.impl;

import com.example.webbansach_backend.Entity.*;
import com.example.webbansach_backend.Enum.LoaiMaGiamGia;
import com.example.webbansach_backend.Enum.StatusLogOrder;
import com.example.webbansach_backend.Enum.TrangThaiGiaoHang;
import com.example.webbansach_backend.Enum.TrangThaiMaGiamGia;
import com.example.webbansach_backend.Repository.*;
import com.example.webbansach_backend.dto.*;
import com.example.webbansach_backend.exception.OutOfStockException;
import com.example.webbansach_backend.exception.VoucherStateException;
import com.example.webbansach_backend.service.CartService;
import com.example.webbansach_backend.service.MaGiamGiaService;
import com.example.webbansach_backend.service.OrderService;
import com.example.webbansach_backend.utils.ParseJacksonUtil;
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
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;


@Service

public class OrderServiceImpl implements OrderService {
    @Autowired
    private GioHangSachRepository gioHangSachRepository ;
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

    // đầu tiên phải check xem ngươiuf dùng có đơn hàng hay chưa . Nếu chưa có thì tạo mơi
    // đây là trường hợp đặt tất cả sản pphaamr trong giỏ hàng .
    @Override
    @Transactional
    public void placeOrderFromCart(String tenDangNhap , DatHangFromCartRequestDTO datHangFromCartRequestDTO){
        GioHang gioHang = cartService.checkGioHang(tenDangNhap) ;
        if(gioHang.getGioHangSaches().isEmpty()) {
            throw new RuntimeException("Giỏ hàng chống!");
        }
        if(datHangFromCartRequestDTO.getDanhSachSanPhamDatHang().isEmpty()){
            throw new RuntimeException("sản phẩm chọn hàng khồn hợp lệ") ;
        }
        // thiêts lập cho đơn hàng
        HinhThucGiaoHang hinhThucGiaoHang = hinhThucGiaoHangRepository.findById(datHangFromCartRequestDTO.getMaHinhThucGiaoHang()).orElseThrow(()->new RuntimeException("Không thấy HÌnh thức giao hàng")) ;
        HinhThucThanhToan hinhThucThanhToan = hinhThucThanhToanRepository.findById(datHangFromCartRequestDTO.getMaHinhThucThanhToan()).orElseThrow(()->new RuntimeException("Không thấy HÌnh thức thamh toán")) ;
        DiaChiGiaoHang diaChiGiaoHang = diaChiGiaoHangRepository.findById(datHangFromCartRequestDTO.getMaDiaChiGiaoHang()).orElseThrow(()->new RuntimeException("Không thấy địa chỉ giao hàng"));
        String diaChi = diaChiGiaoHang.getSoNha() +","+ diaChiGiaoHang.getPhuongOrXa() +","+ diaChiGiaoHang.getQuanOrHuyen() +","+ diaChiGiaoHang.getTinhOrCity() ;
        DonHang donHang = new DonHang() ;
        donHang.setNguoiDung(gioHang.getNguoiDung());
        donHang.setNgayTao(LocalDateTime.now());
        donHang.setTrangThai(TrangThaiGiaoHang.CHO_XAC_NHAN);
        if(hinhThucGiaoHang.getTenHinhThucGiaoHang().equals("Giao hàng nhanh")
                || hinhThucGiaoHang.getTenHinhThucGiaoHang().equals("Giao hàng tiết kiệm")
                || hinhThucGiaoHang.getTenHinhThucGiaoHang().equals("Hỏa tốc") ){
            donHang.setChiPhiGiaoHang(hinhThucGiaoHang.getChiPhiGiaoHang());
        }
        donHang.setHinhThucGiaoHang(hinhThucGiaoHang);
        donHang.setHinhThucThanhToan(hinhThucThanhToan);
        donHang.setDiaChiNhanHang(diaChi);
        // thiết lập cho cho tết đơn hàng

        List<ChiTietDonHang> chiTietDonHangs = new ArrayList<>() ;
        System.out.println("FE gửi : " + datHangFromCartRequestDTO.getDanhSachSanPhamDatHang());
        System.out.println("BE có : " );
        for(GioHangSach gioHangSach: gioHang.getGioHangSaches()){
            System.out.print(gioHangSach.getMaGioHangSach() + " ");

            if(datHangFromCartRequestDTO.getDanhSachSanPhamDatHang().contains(gioHangSach.getMaGioHangSach())){
                Sach sach = sachRepository.findByIdForUpdate(gioHangSach.getSach().getMaSach()).orElseThrow(()-> new RuntimeException("không tìm thấy sách")) ;
                if(gioHangSach.getSoLuong() > sach.getSoLuong()){
                    throw new OutOfStockException("sốl lượng sách tồn kho không đủ")  ;
                }
                sach.setSoLuong(sach.getSoLuong() - gioHangSach.getSoLuong());
                ChiTietDonHang chiTietDonHang = new ChiTietDonHang() ;
                chiTietDonHang.setDonHang(donHang);
                chiTietDonHang.setSach(sach);
                chiTietDonHang.setSoLuong(gioHangSach.getSoLuong());
                chiTietDonHang.setGiaBan(sach.getGiaBan()) ;
                chiTietDonHang.setTongGia(gioHangSach.getSoLuong()*sach.getGiaBan());
                chiTietDonHangs.add(chiTietDonHang) ;
            }

        }
        if(chiTietDonHangs.isEmpty()){
            throw new RuntimeException("Chọn sản phẩm đề đăth hàng") ;
        }
        donHang.setDanhSachChiTietDonHang(chiTietDonHangs);
        donHangRepository.save(donHang) ;

        // xử lý xóa sản phẩm khỏi giỏ hàng : chỗ này không xóa đơn lẻ vì nó đang đc quản lý bởi cha nên dù có xóa cx sẽ bị flush lại
        Set<Long> ids = datHangFromCartRequestDTO.getDanhSachSanPhamDatHang() ; // lấy ra những cái cần xóa
        // sau đó xóa theo điều kiên của collection.removeIf() ;
        gioHang.getGioHangSaches().removeIf(gioHangSach -> ids.contains(gioHangSach.getMaGioHangSach())) ;

    }


    // rào chắn chống truy cập quá mức vào DB
    @Override
    public void placeOder( String tenDangNhap,DatHangRequestDTO datHangRequestDTO) {
        // keys

        String key1 = "book:"+datHangRequestDTO.getMaSach() ;
        String key2 = "order-stream" ;
        List<String> keys = Arrays.asList(key1 , key2) ;
        String request_id = UUID.randomUUID().toString() ;

        // sử lý đătj hàng mà không dùng mã giảm giá
        String maGiam = datHangRequestDTO.getMaGiam() == null ? "null": datHangRequestDTO.getMaGiam().toString() ;

        Long result = redisTemplate.execute(stockOrder ,keys ,
                request_id,
                datHangRequestDTO.getSoLuong(),
                datHangRequestDTO.getMaSach(),
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

        System.out.println("request : " + request_id);
    }

    @Override
    @Transactional
    public void capNhatTrangThaiDonHang(int maDonHang, TrangThaiGiaoHang trangThai){
        DonHang donHang = donHangRepository.findById(maDonHang).orElseThrow() ;
        donHang.setTrangThai(trangThai);
    }
    @Override
    @Transactional
    public List<DonHangTrangThaiResponeDTO> getDonHangTheoTrangThai(String tenDangNhap , TrangThaiGiaoHang trangThaiGiaoHang) {
        List<DonHang> donHangs = donHangRepository.findByNguoiDung_TenDangNhapAndTrangThai( tenDangNhap, trangThaiGiaoHang) ;

        List<DonHangTrangThaiResponeDTO> result = new ArrayList<>() ;
        for(DonHang donHang : donHangs){
            DonHangTrangThaiResponeDTO donHangTrangThaiResponeDTO = modelMapper.map(donHang , DonHangTrangThaiResponeDTO.class) ;
            List<ChiTietDonHang> chiTietDonHangs = donHang.getDanhSachChiTietDonHang() ;
            List<SachTrongDonDTO> sachTrongDonDTOS = new ArrayList<>() ;
            for(ChiTietDonHang chiTietDonHang : chiTietDonHangs){
                SachTrongDonDTO sachTrongDonDTO = modelMapper.map(chiTietDonHang , SachTrongDonDTO.class) ;
                sachTrongDonDTO.setMaSach(chiTietDonHang.getSach().getMaSach());
                sachTrongDonDTO.setTenSach(chiTietDonHang.getSach().getTenSach());
                sachTrongDonDTOS.add(sachTrongDonDTO) ;

            }
            donHangTrangThaiResponeDTO.setSachTrongDonDTOS(sachTrongDonDTOS);
            result.add(donHangTrangThaiResponeDTO) ;

        }
        return result ;
    }
    @Override
    @Transactional
    public void thaoTacDonHang(String tenDangNhap , int maDonHang){
        DonHang donHang = donHangRepository.findByNguoiDung_TenDangNhapAndMaDonHang(tenDangNhap , maDonHang).orElseThrow(()->new RuntimeException("Don hàng không tồn tại")) ;
        if(donHang.getTrangThai().equals(TrangThaiGiaoHang.DA_HUY)){


            for(ChiTietDonHang chiTietDonHang : donHang.getDanhSachChiTietDonHang()){
                Sach sach = sachRepository.findByMaSach(chiTietDonHang.getSach().getMaSach()).orElseThrow() ;
                if(sach.getSoLuong() >= chiTietDonHang.getSoLuong()){
                    sach.setSoLuong(sach.getSoLuong() - chiTietDonHang.getSoLuong());
                }else {
                    throw new RuntimeException("Số lượng sachs khòng đủ để đặt hàng") ;
                }
            }
            donHang.setTrangThai(TrangThaiGiaoHang.CHO_XAC_NHAN);
        }else if(donHang.getTrangThai().equals(TrangThaiGiaoHang.CHO_XAC_NHAN)){
            List<ChiTietDonHang> chiTietDonHangs = donHang.getDanhSachChiTietDonHang() ;
            for(ChiTietDonHang chiTietDonHang : chiTietDonHangs){
                Sach sach = sachRepository.findByMaSach(chiTietDonHang.getSach().getMaSach()).orElseThrow() ;
                sach.setSoLuong(sach.getSoLuong() + chiTietDonHang.getSoLuong());
            }
            donHang.setTrangThai(TrangThaiGiaoHang.DA_HUY);
        }else {
            throw new RuntimeException("khong thể thao tác với trạng thái này , thao tác không hợp lệ") ;
        }

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
    @Scheduled(fixedDelay = 100)  //
    public void consumeNewMessage(){
        List<MapRecord<String , Object , Object>> messages = redisTemplate.opsForStream().read(
                Consumer.from("order-group" , "consumer-1") ,
                StreamReadOptions.empty().count(10).block(Duration.ofSeconds(2)) ,
                StreamOffset.create("order-stream" , ReadOffset.lastConsumed())
        ) ;

        if(messages == null) return ;

        // xử lý đơn hàng ở đây
        messages.forEach(this::handleMessage);
    }
    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void retryOrder(){

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
            if(pendingMessage.getTotalDeliveryCount() >=10){
                List<MapRecord<String , Object,Object>> claimed  = redisTemplate.opsForStream().claim(
                        "order-stream" ,
                        "order-group" ,
                        "consumer-1" ,
                        Duration.ofSeconds(10) ,
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
                claimed.forEach(this::handleMessage);
            }

        }
    }
    @Transactional
    public void handleMessage(MapRecord<String , Object , Object> message ){

        boolean exist = logOrderRepository.existsByRequestId(ParseJacksonUtil.toString(message.getValue().get("request_id").toString()));
        // idempotent DB
        if(exist){
            redisTemplate.opsForStream().acknowledge("order-stream" , "order-group" , message.getId()) ;
            return ;
        }
        processOrder(message); // nó sẽ chạy chung 1 transition với handleMessage chứ k chạy cái transis=tion của riêng nó .


        LogOrder entity = new LogOrder() ;
        entity.setCreateAt(LocalDateTime.now());
        String  maGiam = ParseJacksonUtil.toString(message.getValue().get("maGiam").toString()) ;
        System.out.println("ma giam la:"+maGiam);
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
                        System.out.println("tạo đơn và log thành công");

                        String maGiam = ParseJacksonUtil.toString(message.getValue().get("maGiam").toString()) ;
                        String tenDangNhap = ParseJacksonUtil.toString(message.getValue().get("tenDangNhap").toString()) ;
                        String request_id = ParseJacksonUtil.toString(message.getValue().get("request_id").toString()) ;

                        if(!maGiam.equals("null")) {
                            int value = Integer.parseInt(maGiam) ;
                            maGiamGiaService.dungMaGiamGiaUser(tenDangNhap , value ,request_id );
                        }

                        //ACK
                        redisTemplate.opsForStream().acknowledge("order-stream" ,"order-group" , message.getId()) ;
                    }
                }
        );

    }

    public void processOrder(MapRecord<String , Object , Object> message) {
        // Kiểm tra người dùng
        NguoiDung nguoiDung = nguoiDungRepository.findByTenDangNhap(ParseJacksonUtil.toString(message.getValue().get("tenDangNhap").toString())).orElseThrow() ;

        // permisstic lock
        Sach sach = sachRepository.findByIdForUpdate(Integer.parseInt(message.getValue().get("maSach").toString())).orElseThrow(()->new RuntimeException("Sách không tồn tại")) ;
        if(sach.getSoLuong() < Integer.parseInt(message.getValue().get("soLuong").toString())){
            throw  new RuntimeException("sách trong kho không đủ số lượng để bán") ;
        }
        sach.setSoLuong(sach.getSoLuong() - Integer.parseInt( message.getValue().get("soLuong").toString()));

        HinhThucThanhToan hinhThucThanhToan = hinhThucThanhToanRepository.findById(Integer.parseInt(message.getValue().get("maHinhThucThanhToan").toString())).orElseThrow(()->new RuntimeException("Không thấy HÌnh thức thamh toán")) ;
        HinhThucGiaoHang hinhThucGiaoHang = hinhThucGiaoHangRepository.findById(Integer.parseInt(message.getValue().get("maHinhThucGiaoHang").toString())).orElseThrow(()->new RuntimeException("Không thấy HÌnh thức giao hàng")) ;
        /// thiết lập địa chỉ giao hàng
        DiaChiGiaoHang diaChiGiaoHang = diaChiGiaoHangRepository.findById(Integer.parseInt(message.getValue().get("maDiaChiGiaoHang").toString())).orElseThrow(()->new RuntimeException("Không thấy địa chỉ giao hàng"));
        String diaChi = diaChiGiaoHang.getSoNha() +","+ diaChiGiaoHang.getPhuongOrXa() +","+ diaChiGiaoHang.getQuanOrHuyen() +","+ diaChiGiaoHang.getTinhOrCity() ;

        String  maGiam = ParseJacksonUtil.toString(message.getValue().get("maGiam").toString()) ;

        // tạo đơn hàng
        DonHang donHang = new DonHang() ;
        donHang.setNguoiDung(nguoiDung);
        donHang.setNgayTao(LocalDateTime.now());
        // chỗ này nên điều kiện nếu nó là hình thức giao hnagf nhanh  hay chậm ... thì mk sẽ cso cái phí giao hàng khác nhau
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

        // check giamr giá tiền đơn hàng
        if(maGiam.equals("null")){
            donHang.setTongGia(sach.getGiaBan());
        }else {
            MaGiamGia maGiamGia =maGiamGiaService.getMaGiamGia(Integer.parseInt(maGiam)) ;
            if(maGiamGia.getTrangThaiMaGiamGia() == TrangThaiMaGiamGia.KHOA || maGiamGia.getTrangThaiMaGiamGia() == TrangThaiMaGiamGia.HET_HAN){
                throw new VoucherStateException("mã giảm giá không còn hoạt động") ;
            }
            if(maGiamGia.getLoaiMaGiamGia() == LoaiMaGiamGia.PHAN_TRAM){
                if(sach.getGiaBan() >= maGiamGia.getDonGiaTu()){
                    double tienGiam = (double) (sach.getGiaBan() * maGiamGia.getPhanTramGiam()) /100 ;
                    if(tienGiam <= maGiamGia.getGiamToiDa()) donHang.setTongGia(sach.getGiaBan() -  tienGiam );
                    else if(tienGiam > maGiamGia.getGiamToiDa()) donHang.setTongGia(sach.getGiaBan() -  maGiamGia.getGiamToiDa());
                }else donHang.setTongGia(sach.getGiaBan());
            }else if(maGiamGia.getLoaiMaGiamGia() == LoaiMaGiamGia.TIEN){
                double tienGiam = sach.getGiaBan() - maGiamGia.getTienGiam() ;
                if(sach.getGiaBan() >= maGiamGia.getDonGiaTu()) donHang.setTongGia(tienGiam < 0 ? 0 :tienGiam );
                else if(sach.getGiaBan() < maGiamGia.getDonGiaTu()) donHang.setTongGia(sach.getGiaBan());
            }
        }

        // tạo chi tiết đơn và lưu vào DB
        ChiTietDonHang chiTietDonHang = new ChiTietDonHang() ;
        chiTietDonHang.setDonHang(donHang);
        chiTietDonHang.setSach(sach);
        chiTietDonHang.setSoLuong(Integer.parseInt( message.getValue().get("soLuong").toString()));
        chiTietDonHang.setGiaBan(sach.getGiaBan()) ;
        chiTietDonHang.setTongGia(sach.getGiaBan() * Integer.parseInt( message.getValue().get("soLuong").toString()));

        donHang.getDanhSachChiTietDonHang().add(chiTietDonHang) ;
        donHangRepository.save(donHang) ; // có thể bỏ cái này cx k sao .

        // stats : thống kê doanh số
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy/MM/dd") ;
        String key = "stats:" + LocalDateTime.now().format(format) ;
        redisTemplate.opsForHash().increment(key , "orders" , 1) ;
        redisTemplate.opsForHash().increment(key , "books" ,Integer.parseInt(message.getValue().get("soLuong").toString())) ;
        redisTemplate.opsForHash().increment(key , "revenue" ,donHang.getTongGia()) ;
        // trường hơp không dunmgf mã giảm giá

        if(maGiam.equals("null")){
            donHang.setTrangThai(TrangThaiGiaoHang.DA_XAC_NHAN);
        }
    }

    @Transactional
    @Scheduled(fixedDelay = 5000) // 5s bù kho một lần
    public void compensateStockBook(){

        List<MapRecord<String, Object, Object>> messages = redisTemplate.opsForStream().read(
                Consumer.from("order-dead-letter-group", "consumer-1"),
                StreamReadOptions.empty().count(10).block(Duration.ofSeconds(2)),
                StreamOffset.create("order-dead-letter", ReadOffset.lastConsumed())
        );
        if (messages == null || messages.isEmpty()) return;

        // lấy ra danh sách id voucher để check dưới DB rồi reconcicle
        Set<Integer> idBooks = new HashSet<>() ;
        for (MapRecord<String, Object, Object> message : messages) {
            idBooks.add(Integer.parseInt(message.getValue().get("maSach").toString()));
        }

        List<Sach> saches = sachRepository.findByMaSachIn(idBooks) ;
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
