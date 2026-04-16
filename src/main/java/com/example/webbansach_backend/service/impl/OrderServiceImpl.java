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
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;


@Service

public class OrderServiceImpl implements OrderService {
    @Autowired
    private CartService cartService;
    @Autowired
    private DonHangRepository donHangRepository;
    @Autowired
    private SachRepository sachRepository;
    @Autowired
    private NguoiDungRepository nguoiDungRepository;
    @Autowired
    private HinhThucThanhToanRepository hinhThucThanhToanRepository;
    @Autowired
    private HinhThucGiaoHangRepository hinhThucGiaoHangRepository;
    @Autowired
    private DiaChiGiaoHangRepository diaChiGiaoHangRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    @Qualifier("stockOrder")
    private DefaultRedisScript<Long> stockOrder;
    @Autowired
    private LogOrderRepository logOrderRepository;
    @Autowired
    private MaGiamGiaService maGiamGiaService;
    @Autowired
    private ThongKeBanHangService thongKeBanHangService;
    @Autowired
    private ReturnOrderTimeoutBatchService returnOrderTimeoutBatchService;
    @Autowired
    private ObjectMapper objectMapper;


    // atomic
    @Override
    @Transactional
    public void placeOder(String tenDangNhap, DatHangRequestDTO datHangRequestDTO) throws JsonProcessingException {
        // sẽ két hợp với việc chjoongs spam API đối vs 1 user để k bị đặt hàng chùng - > trải nghiêm tệ đối với user
        // keys
        ObjectMapper objectMapper = new ObjectMapper();
        String itemJson = objectMapper.writeValueAsString(datHangRequestDTO.getItems());
        String key1 = "stock:book:{ws}:";
        // cách loại key khác như : reate_limit:order:ip:{ip} ;
        List<String> keys = Arrays.asList(key1);
        // sử lý đătj hàng mà không dùng mã giảm giá
        String maGiam = datHangRequestDTO.getMaGiam() == null ? "null" : datHangRequestDTO.getMaGiam().toString();
        Long result = redisTemplate.execute(stockOrder, keys,
                itemJson
                );
        if (result == -1) throw new RuntimeException("kho không tồn tại");
        if (result == -2) throw new RuntimeException("kho bị âm");
        if (result == -4) throw new RuntimeException("số lượng muốn mua phải là chữu số");
        if (result == -3) throw new RuntimeException("số lượng muốn mua phải laf số dương (klhoong được âm)");
        if (result == 0) throw new RuntimeException("kho không đủ");
        if (result == -5) throw new RuntimeException(tenDangNhap + " Spam API quá số lần quy định");
        if (result == -9) throw new RuntimeException("số lượng mua không hợp lệ");
        System.out.println("[" + TimeLogUtil.toTimeSystemLog() + "]" + " user:" + tenDangNhap + "đặt đơn hàng");


        int shard = Math.abs(tenDangNhap.hashCode()) % 8; // chia đều thanh 8 straem để đẽ quản lý
        String request_id = UUID.randomUUID().toString();
        String key2 = "order-stream:{" + "shard-" + shard + "}";
        Map<String, String> record = new HashMap<>();
        record.put("request_id", request_id);
        record.put("tenDangNhap", tenDangNhap);
        record.put("items", itemJson);
        record.put("maGiam", maGiam);
        record.put("maDiaChiGiaoHang", String.valueOf(datHangRequestDTO.getMaDiaChiGiaoHang()));
        record.put("maHinhThucThanhToan", String.valueOf(datHangRequestDTO.getMaHinhThucThanhToan()));
        record.put("maHinhThucGiaoHang", String.valueOf(datHangRequestDTO.getMaHinhThucGiaoHang()));

        redisTemplate.opsForStream().add(key2, record);
        System.out.println("order-stream:{shard-"+shard+"}");
    }

    // tính năng của admin
    @Override
    @Transactional
    public void capNhatTrangThaiDonHang(String tenDangNhap, int maDonHang, TrangThaiGiaoHang trangThai) {
        NguoiDung nguoiDung = nguoiDungRepository.findByTenDangNhap(tenDangNhap).
                orElseThrow(() -> new RuntimeException("NGuoi dung không tồn tại"));
        if (!CheckRoleUItil.checkRoleAdminOrUser(nguoiDung))
            throw new RuntimeException("Người dùng khôpng đủ quyền để cập nhật đơn hàng");
        DonHang donHang = donHangRepository.findById(maDonHang).orElseThrow();
        donHang.setTrangThai(trangThai);
    }

    // xem lại chỗ này N + 1 : chi tiết đơn hàng - đơn hànggg
    // kiẻm trA inddex xem đã đúng chưa . xem còn cách khác khong .
    @Override
    @Transactional
    public List<DonHangTrangThaiResponeDTO> getDonHangTheoTrangThai(String tenDangNhap, TrangThaiGiaoHang trangThaiGiaoHang) {
        NguoiDung nguoiDung = nguoiDungRepository.findByTenDangNhap(tenDangNhap).orElseThrow();
        List<DonHang> donHangs = donHangRepository.findByNguoiDungAndTrangThai(nguoiDung, trangThaiGiaoHang);
        if (donHangs.isEmpty()) return null;
        List<DonHangTrangThaiResponeDTO> result = new ArrayList<>();
        for (DonHang donHang : donHangs) {
            DonHangTrangThaiResponeDTO donHangTrangThaiResponeDTO = modelMapper.map(donHang, DonHangTrangThaiResponeDTO.class);
            donHangTrangThaiResponeDTO.setHoTen(nguoiDung.getHoDem() + nguoiDung.getTen());
            donHangTrangThaiResponeDTO.setSoDienThoai(nguoiDung.getSoDienThoai());
            List<SachTrongDonDTO> sachTrongDonDTO = new ArrayList<>();
            for (ChiTietDonHang chiTietDonHang : donHang.getDanhSachChiTietDonHang()) {
                SachTrongDonDTO sachTrongDon = modelMapper.map(chiTietDonHang, SachTrongDonDTO.class);
                sachTrongDon.setTenSach(chiTietDonHang.getSach().getTenSach());
                sachTrongDon.setMaSach(chiTietDonHang.getSach().getMaSach());
                sachTrongDonDTO.add(sachTrongDon);
            }
            donHangTrangThaiResponeDTO.setSachTrongDonDTOS(sachTrongDonDTO);
            result.add(donHangTrangThaiResponeDTO);

        }
        return result;
    }

    @Override
    @Transactional
    // multithread
    public void thaoTacDonHang(String tenDangNhap, int maDonHang) {
        int totalBook = 0;
        DonHang donHang = donHangRepository.findByNguoiDung_TenDangNhapAndMaDonHang(tenDangNhap, maDonHang).
                orElseThrow(() -> new RuntimeException("Don hàng không tồn tại"));
        if (donHang.getTrangThai().equals(TrangThaiGiaoHang.DA_HUY)) {
            for (ChiTietDonHang chiTietDonHang : donHang.getDanhSachChiTietDonHang()) {
                // permistic_lock
                Sach sach = sachRepository.findByIdForUpdate(chiTietDonHang.getSach().getMaSach()).orElseThrow();
                if (sach.getSoLuong() >= chiTietDonHang.getSoLuong() && sach.isActive()) {
                    totalBook = +chiTietDonHang.getSoLuong();
                    sach.setSoLuong(sach.getSoLuong() - chiTietDonHang.getSoLuong());
                } else {
                    throw new RuntimeException("Số lượng sachs khòng đủ để đặt hàng");
                }
            }
            System.out.println("[" + TimeLogUtil.toTimeSystemLog() + "]" + " user:" + tenDangNhap + ":đặt lại đơn hàng:" + maDonHang);
            donHang.setTrangThai(TrangThaiGiaoHang.DA_XAC_NHAN);
        } else if (donHang.getTrangThai().equals(TrangThaiGiaoHang.CHO_XAC_NHAN)) {
            List<ChiTietDonHang> chiTietDonHangs = donHang.getDanhSachChiTietDonHang();
            for (ChiTietDonHang chiTietDonHang : chiTietDonHangs) {
                // lock
                Sach sach = sachRepository.findByIdForUpdate(chiTietDonHang.getSach().getMaSach()).orElseThrow();
                sach.setSoLuong(sach.getSoLuong() + chiTietDonHang.getSoLuong());
            }
            System.out.println("[" + TimeLogUtil.toTimeSystemLog() + "]" + " user:" + tenDangNhap + ":hủy đơn:" + maDonHang);
            donHang.setTrangThai(TrangThaiGiaoHang.DA_HUY);
        } else {
            throw new RuntimeException("khong thể thao tác với trạng thái này , thao tác không hợp lệ");
        }
        int finalTotalBook = totalBook;
        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        thongKeBanHangService.statWhenPlaceOrder(finalTotalBook, donHang);
                    }
                }
        );

    }

    @PostConstruct
    public void initOrderStream() {
        try {
            redisTemplate.opsForStream().createGroup("order-stream:{shard-0}", ReadOffset.latest(), "group:shard-0");
            redisTemplate.opsForStream().createGroup("order-stream:{shard-1}", ReadOffset.latest(), "group:shard-1");
            redisTemplate.opsForStream().createGroup("order-stream:{shard-2}", ReadOffset.latest(), "group:shard-2");
            redisTemplate.opsForStream().createGroup("order-stream:{shard-3}", ReadOffset.latest(), "group:shard-3");
            redisTemplate.opsForStream().createGroup("order-stream:{shard-4}", ReadOffset.latest(), "group:shard-4");
            redisTemplate.opsForStream().createGroup("order-stream:{shard-5}", ReadOffset.latest(), "group:shard-5");
            redisTemplate.opsForStream().createGroup("order-stream:{shard-6}", ReadOffset.latest(), "group:shard-6");
            redisTemplate.opsForStream().createGroup("order-stream:{shard-7}", ReadOffset.latest(), "group:shard-7");

            redisTemplate.opsForStream().createGroup("order-stream-dead:{shard-0}", ReadOffset.latest(), "group:shard-0");
            redisTemplate.opsForStream().createGroup("order-stream-dead:{shard-1}", ReadOffset.latest(), "group:shard-1");
            redisTemplate.opsForStream().createGroup("order-stream-dead:{shard-2}", ReadOffset.latest(), "group:shard-2");
            redisTemplate.opsForStream().createGroup("order-stream-dead:{shard-3}", ReadOffset.latest(), "group:shard-3");
            redisTemplate.opsForStream().createGroup("order-stream-dead:{shard-4}", ReadOffset.latest(), "group:shard-4");
            redisTemplate.opsForStream().createGroup("order-stream-dead:{shard-5}", ReadOffset.latest(), "group:shard-5");
            redisTemplate.opsForStream().createGroup("order-stream-dead:{shard-6}", ReadOffset.latest(), "group:shard-6");
            redisTemplate.opsForStream().createGroup("order-stream-dead:{shard-7}", ReadOffset.latest(), "group:shard-7");
        } catch (Exception ignoreException) {
            // có rồi thì thôi .
        }
    }



    @Override
    @Transactional
    public void saveBatch(List<MapRecord<String , Object , Object>> messages , int shard) throws JsonProcessingException {
        Map<Integer , HinhThucThanhToan> hinhThucThanhToanMap= hinhThucThanhToanRepository.findAll()
                .stream().collect(Collectors.toMap(HinhThucThanhToan::getMaHinhThucThanhToan, s->s)) ;
        Map<Integer ,HinhThucGiaoHang> hinhThucGiaoHangMap = hinhThucGiaoHangRepository.findAll()
                .stream().collect(Collectors.toMap(HinhThucGiaoHang::getMaHinhThucGiaoHang ,s->s));
        Map<Integer,DiaChiGiaoHang> diaChiGiaoHangMap = diaChiGiaoHangRepository.findAll()
                .stream().collect(Collectors.toMap(DiaChiGiaoHang::getMaDiaChiGiaoHang ,s->s)) ;
        // gom id sách  va tenDangNhap
        Set<String> tenDangNhaps = new HashSet<>() ;
        Set<Integer> idSaches = new HashSet<>() ;
        for(MapRecord<String , Object , Object> message: messages){
            tenDangNhaps.add((message.getValue().get("tenDangNhap").toString())) ;
            String itemJson = (message.getValue().get("items").toString());
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
            String request_Id = (message.getValue().get("request_id").toString()) ;
            String maGiam = (message.getValue().get("maGiam").toString()) ;
            if(isLogOrder(message ,request_Id , shard)) continue;
            // create
            DonHang donHang = makeDonHang(message, maGiam , sachMap , nguoiDungMap ,hinhThucThanhToanMap,hinhThucGiaoHangMap ,diaChiGiaoHangMap );
            LogOrder log =makeLogOrder(message ,request_Id ,maGiam) ;
            // add list
            logs.add(log) ;
            donHangs.add(donHang) ;

            // add field need to act after commit
            actionAfterCommit.add(Map.of(
                    "maGiam" , maGiam,
                    "tenDangNhap" ,(message.getValue().get("tenDangNhap").toString()),
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
                        redisTemplate.opsForStream().acknowledge("order-stream:{shard-"+shard+"}" ,"group:shard-"+ shard,recordIds.toArray(new RecordId[0]) ) ;

                    }
                }
        );
    }

    // bù kho redis
//    @Transactional
//    @Scheduled(fixedDelay = 60000) // 60s bù kho một lần
//    public void compensateStockRedisBook() throws JsonProcessingException {
//
//    }
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
        String request_Id = (message.getValue().get("request_id").toString()) ;
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
    private DonHang makeDonHang(MapRecord<String , Object ,Object> message ,
                                String maGiam ,
                                Map<Integer ,Sach>  sachMap ,
                                Map<String , NguoiDung> nguoiDungMap ,
                                Map<Integer , HinhThucThanhToan> hinhThucThanhToanMap,
                                Map<Integer ,HinhThucGiaoHang> hinhThucGiaoHangMap,
                                Map<Integer,DiaChiGiaoHang> diaChiGiaoHangMap) throws JsonProcessingException {
        // Kiểm tra người dùng từ batch
        String tenDangNhap =(message.getValue().get("tenDangNhap").toString()) ;
        NguoiDung nguoiDung = nguoiDungMap.get(tenDangNhap) ;
        if(nguoiDung == null) throw new RuntimeException("Nguoi dùng không tônf tại") ;
        // parse json -> object List
        String iteamJson =message.getValue().get("items").toString() ;
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
            log.setVoucherID(Integer.parseInt((message.getValue().get("maGiam").toString()))) ;
        }
        log.setRequestId(request_Id) ;
        return log ;
    }
    private boolean isLogOrder( MapRecord<String , Object ,Object> message,String request_Id , int shard){
        boolean exist = logOrderRepository.existsByRequestId(request_Id);
        if(exist){
            redisTemplate.opsForStream().acknowledge("order-stream:{shard-"+shard+"}" , "group:shard-"+shard , message.getId()) ;
            return true ;
        }
        return false ;
    }

}