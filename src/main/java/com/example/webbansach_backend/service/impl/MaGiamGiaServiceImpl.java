
package com.example.webbansach_backend.service.impl;

import com.example.webbansach_backend.Entity.*;
import com.example.webbansach_backend.Enum.*;
import com.example.webbansach_backend.Repository.*;
import com.example.webbansach_backend.dto.*;
import com.example.webbansach_backend.exception.NotFoundException;
import com.example.webbansach_backend.exception.OutOfStockException;
import com.example.webbansach_backend.exception.VoucherStateException;
import com.example.webbansach_backend.mapper.MaGiamGiaMapper;
import com.example.webbansach_backend.mapper.MaGiamGiaUserMapper;
import com.example.webbansach_backend.service.MaGiamGiaService;
import com.example.webbansach_backend.service.OrderService;
import com.example.webbansach_backend.utils.ParseJacksonUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.transaction.Transactional;
import org.flywaydb.core.internal.util.JsonUtils;
import org.hibernate.resource.transaction.backend.jta.internal.synchronization.RegisteredSynchronization;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.crypto.spec.PSource;
import javax.sound.midi.Soundbank;
import java.lang.reflect.Array;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class MaGiamGiaServiceImpl implements MaGiamGiaService {
    @Autowired
    private NguoiDungRepository nguoiDungRepository ;
    @Autowired
    private SachRepository sachRepository ;
    @Autowired
    private ModelMapper modelMapper ;
    @Autowired
    private MaGiamGiaNguoiDungRepository maGiamGiaNguoiDungRepository ;
    @Autowired
    private MaGiamGiaRepository maGiamGiaRepository ;
    @Autowired
    private MaGiamGiaMapper maGiamGiaMapper ;
    @Autowired
    private MaGiamGiaSachRepository maGiamGiaSachRepository ;
    @Autowired
    private RedisTemplate<String , Object> redisTemplate ;
    @Autowired
    private MaGiamGiaUserMapper maGiamGiaUserMapper ;
    @Autowired
    @Qualifier("stockVoucher")
    private DefaultRedisScript<Long> stockVoucher ;
    @Autowired
    private LogOrderRepository logOrderRepository ;
    @Autowired
    private DonHangRepository donHangRepository ;

    // chỗ này chú ý rằng nó sẽ có mã như 1 chuỗi ký tự . VD : "1,2,3" . khi lấy phải trùng y hệt "1,2,3" thì ms lấy đc .
    // "1" hay "2,3" cx k bao giừo lấy đc . vì nó có dạng chuỗi để so khớp cho nhanh nhất có thể
    @Cacheable(
            value = "maGiamGiaSach" ,
            key = "#danhSachMaSach.stream().sorted().collect(T(java.util.stream.Collectors).joining(','))"
    )
    @Override
    public List<MaGiamGiaResponeDTO> getMaGiamGiaCuaSach(List<Integer> danhSachMaSach) {
        List<Sach> saches = sachRepository.findByMaSachInFetch(danhSachMaSach) ;
        if(saches.isEmpty()) throw new NotFoundException("Sách không tồn tại") ;
        List<MaGiamGiaResponeDTO> maGiamGiaResponeDTOS = new ArrayList<>() ;
        // láy mã giảm giá theo sách
        for(Sach sach : saches){

            Set<MaGiamGiaSach> maGiamGiaSaches = sach.getMaGiamGiaSaches() ;
            MaGiamGiaResponeDTO maGiamGiaResponeDTO = new MaGiamGiaResponeDTO() ;
            maGiamGiaResponeDTO.setMaSach(sach.getMaSach());
            List<MaGiamGiaCuaSachRespone> maGiamGiaCuaSachRespones = new ArrayList<>() ;
            for(MaGiamGiaSach maGiamGiaSach : maGiamGiaSaches){
                if(maGiamGiaSach.getMaGiamGia().getSoLuong() > maGiamGiaSach.getMaGiamGia().getSoMaDaDung()
                        && maGiamGiaSach.getMaGiamGia().getTrangThaiMaGiamGia() == TrangThaiMaGiamGia.DANG_HOAT_DONG){
                    MaGiamGiaCuaSachRespone maGiamGiaCuaSachRespone =maGiamGiaMapper.toDTO(maGiamGiaSach.getMaGiamGia()) ;
                    maGiamGiaCuaSachRespones.add(maGiamGiaCuaSachRespone) ;
                }
            }
            maGiamGiaResponeDTO.setMaGiamGiaCuaSachRespones(maGiamGiaCuaSachRespones);
            maGiamGiaResponeDTOS.add(maGiamGiaResponeDTO) ;
        }


        return maGiamGiaResponeDTOS ;

    }


    //lấy toàn bộ mã giảm giá lên đối với đối tượng người dùng
    @Override
    public MaGiamGiaUserResponeDTO getMaGiamGiaCuaNguoiDung(String tenDangNhap) {
        NguoiDung nguoiDung = nguoiDungRepository.findByTenDangNhap(tenDangNhap).orElseThrow() ;
        String key = "user:voucher:"+tenDangNhap ;
        MaGiamGiaUserResponeDTO cached = (MaGiamGiaUserResponeDTO) redisTemplate.opsForValue().get(key) ;

        if(cached != null){
            System.out.println("ĐÃ truy cập vào cache vào lất DATA lên");
            return cached ;
        }
        System.out.println("Chưa sưr dụng cache để lấy DATA ");
        // lấy tất cả mã giảm giá lên
        List<MaGiamGia> maGiamGias = maGiamGiaRepository.findMaGiamGiaActive(TrangThaiMaGiamGia.DANG_HOAT_DONG ,DoiTuongApDungMa.NGUOI_DUNG , nguoiDung) ;

        List<MaGiamGiaCuaUserResponeDTO> maGiamGiaCuaUserResponeDTOS = maGiamGias.stream().map(maGiamGiaUserMapper::toDto).toList() ;

        MaGiamGiaUserResponeDTO maGiamGiaUserResponeDTO = new MaGiamGiaUserResponeDTO() ;
        maGiamGiaUserResponeDTO.setMaNguoiDung(nguoiDung.getMaNguoiDung());
        maGiamGiaUserResponeDTO.setMaGiamGiaCuaUserResponeDTOS(maGiamGiaCuaUserResponeDTOS);

        redisTemplate.opsForValue().set(key ,maGiamGiaUserResponeDTO , 5 , TimeUnit.MINUTES );

        return maGiamGiaUserResponeDTO ;
    }


//    xóa mã giảm gía khỏi cache khi nó đc đc dùng


    @Override
    @Transactional
    public void dungMaGiamGiaUser(String tenDangNhap , int maGiam , String request_id ){

        MaGiamGia maGiamGia = maGiamGiaRepository.findByMaGiam(maGiam).orElseThrow() ;
        if(maGiamGia.getTrangThaiMaGiamGia()== TrangThaiMaGiamGia.KHOA || maGiamGia.getTrangThaiMaGiamGia()== TrangThaiMaGiamGia.HET_HAN  ){
            throw new VoucherStateException("mã giảm giá đã đóng") ;
        }
        // danh sách keys
        String key1 = "voucher:stock:"+maGiam ; // key kho còn bao nhiêu mã
        String key2 = "voucher:user:"+maGiam+":"+tenDangNhap ; // key use đã dùng
        String key3 = "voucher-stream" ; // key stream
        List<String> keys= Arrays.asList(key1,key2,key3);
        System.out.println(tenDangNhap);
        Long result = redisTemplate.execute(stockVoucher ,keys , maGiamGia.getGioiHanSoLuongDungUser() , maGiam , tenDangNhap , request_id);
        if(result == 0){
            throw new RuntimeException("kho không đủ") ;
        }
        if(result == -1){
            throw new RuntimeException("kho không tồn tại") ;
        }
        if(result == -2){
            throw new RuntimeException("user đã hết lượt dùng / quá số lần quy định ") ;
        }
        if(result == -3) {
            throw new RuntimeException("số lượt đã dùng voucher có vấn đề ") ;
        }
        if(result == null){
            throw new RuntimeException("Lỗi Redis cache") ;
        }
        if(result == -4) throw new RuntimeException("Dữ liệu không phải số") ;
        if(result == -5) throw new RuntimeException("dữ liệu userUsed lỗi") ;
        if(result == -6) throw new RuntimeException("ARGV lỗi") ;
    }

@Caching(
        evict = {
                @CacheEvict(value = "maGiamGiaSach" , allEntries = true , condition = "#maGiamGiaRequestDTO.doiTuongApDungMa == T(com.example.webbansach_backend.Enum.DoiTuongApDungMa).SACH") ,
                @CacheEvict(value = "maGiamGiaUser" , allEntries = true , condition = "#maGiamGiaRequestDTO.doiTuongApDungMa == T(com.example.webbansach_backend.Enum.DoiTuongApDungMa).NGUOI_DUNG")
        }
)
@Override
@Transactional
public void themMaGiamGia(MaGiamGiaRequestDTO maGiamGiaRequestDTO){
    MaGiamGia maGiamGia = maGiamGiaMapper.toEntity(maGiamGiaRequestDTO) ;


    if(maGiamGiaRequestDTO.getDoiTuongApDungMa()== DoiTuongApDungMa.SACH){
        if(maGiamGiaRequestDTO.getDanhSachMaSach() != null && !maGiamGiaRequestDTO.getDanhSachMaSach().isEmpty() ){
            maGiamGiaRepository.save(maGiamGia) ;
            List<Sach> saches = sachRepository.findByMaSachIn(maGiamGiaRequestDTO.getDanhSachMaSach()) ;
            if(saches.isEmpty() || saches.size()!= maGiamGiaRequestDTO.getDanhSachMaSach().size()) throw new NotFoundException("Sách không tồn tại / thiếu sách") ;
            List<MaGiamGiaSach> maGiamGiaSaches = new ArrayList<>() ;
            for(Sach sach : saches ){
                MaGiamGiaSach maGiamGiaSach = new MaGiamGiaSach() ;
                maGiamGiaSach.setSach(sach);
                maGiamGiaSach.setMaGiamGia(maGiamGia);
                maGiamGiaSaches.add(maGiamGiaSach) ;
            }
            maGiamGiaSachRepository.saveAll(maGiamGiaSaches) ;
        }else throw new RuntimeException("Sách chưa được chọn đối với mã giảm giá dành cho sách") ;
    }else if(maGiamGiaRequestDTO.getDoiTuongApDungMa()== DoiTuongApDungMa.THE_LOAI){
        if(maGiamGiaRequestDTO.getMaTheLoai() != null){
            maGiamGiaRepository.save(maGiamGia) ;
        }else throw new RuntimeException("ma thể loại bị null") ;

    }else if(maGiamGiaRequestDTO.getDoiTuongApDungMa() == DoiTuongApDungMa.NGUOI_DUNG){
        maGiamGiaRepository.save(maGiamGia) ;
    }else if(maGiamGiaRequestDTO.getDoiTuongApDungMa() == DoiTuongApDungMa.TOAN_HE_THONG){
        maGiamGiaRepository.save(maGiamGia) ;
    }

}

//xóa cache toàn bộ
@CacheEvict(
        value = "maGiamGiaSach" ,
        allEntries = true
)
@Transactional
public void capNhatMaGiamGiaSach(int maGiam , UpdateMaGiamGiaDTO updateMaGiamGiaDTO){
    MaGiamGia maGiamGia =  maGiamGiaRepository.findByMaGiam(maGiam).orElseThrow(()-> new NotFoundException("mã giảm giá không tồn tại")) ;

    if(updateMaGiamGiaDTO.getSoLuong() !=null){
        if(updateMaGiamGiaDTO.getSoLuong() < maGiamGia.getSoMaDaDung()){
            throw new RuntimeException("KHông hợp lệ do đã có số lượng mã dùng lớn hơn so với sô lượng cập nhật") ;
        }
        maGiamGia.setSoLuong(updateMaGiamGiaDTO.getSoLuong());
    }
    if(updateMaGiamGiaDTO.getNgayHetHan()!=null){
        if(updateMaGiamGiaDTO.getNgayHetHan().isBefore(LocalDateTime.now())){
            throw new RuntimeException("Không hợp lệ khi mã vẫn đang còn hạn") ;
        }
        maGiamGia.setNgayHetHan(updateMaGiamGiaDTO.getNgayHetHan());
    }
    if(updateMaGiamGiaDTO.getDonGiaTu()!=null){
        maGiamGia.setDonGiaTu(updateMaGiamGiaDTO.getDonGiaTu());
    }
    if(updateMaGiamGiaDTO.getGiamToiDa()!=null){
        maGiamGia.setGiamToiDa(updateMaGiamGiaDTO.getGiamToiDa());
    }
}

@Scheduled(cron ="0 * * * * ?" )
@Transactional
public void updateVoucherStatusAuto(){

    List<MaGiamGia> maHetHans = maGiamGiaRepository.findByTrangThaiHoatDongAndNgayHetHan(TrangThaiMaGiamGia.DANG_HOAT_DONG , LocalDateTime.now()) ;

    for(MaGiamGia maGiamGia : maHetHans){
        maGiamGia.setTrangThaiMaGiamGia(TrangThaiMaGiamGia.HET_HAN);
    }
}

    @PostConstruct
    public void initStream(){
        try {
            redisTemplate.opsForStream().createGroup("voucher-dead-letter" , ReadOffset.latest() , "voucher-dead-letter-group") ;
            redisTemplate.opsForStream().createGroup("voucher-stream" , ReadOffset.latest() , "voucher-group") ;

        }catch (Exception ignored){
            // nếu srteam đó đã tồn tại thì bắt lỗi và bỏ qua
        }
    }

    @Transactional
    @Scheduled(fixedDelay = 200) // 0,1s chạy ngầm 1 lần
    public void consumeNewMessage(){
        List< MapRecord<String , Object , Object> > messages = redisTemplate.opsForStream().read(
                Consumer.from("voucher-group" , "consumer-1") , // công nhân 1 xin stream gửi mesage
                StreamReadOptions.empty().count(10).block(Duration.ofSeconds(2)) , // mỗi lần lấy tối đa 10 message , đợi tối đa 2s nếu k thấy mesage
                StreamOffset.create("voucher-stream" , ReadOffset.lastConsumed()) // chỉ lấy những message chưa đưa vào group

        );
        if(messages == null) return ;
        // handle ở đây
        messages.forEach(this::handleMessage);
    }
    @Transactional
    @Scheduled(fixedDelay = 5000)
    public void retryPendingMessage(){
        PendingMessagesSummary pendingMessagesSummary = redisTemplate.opsForStream()
                .pending("voucher-stream" , "voucher-group") ;
        if(pendingMessagesSummary == null || pendingMessagesSummary.getTotalPendingMessages() == 0) return ;

        // mỗi lần lấy ra 10 message
        PendingMessages pendingMessages = redisTemplate.opsForStream()
                .pending("voucher-stream" ,
                        Consumer.from("voucher-group" ,"consumer-1") ,
                        Range.unbounded() , 10) ;

        // kiểm tra xem pending nào có idle >= 5s thì cho nó claim laị
        for(PendingMessage pending : pendingMessages){

            if(pending.getTotalDeliveryCount() >= 10 ){
                // lấy chi tiết message để đâye sang dead-letter
                List<MapRecord<String , Object , Object>> claimed = redisTemplate.opsForStream().claim(
                        "voucher-stream",
                        "voucher-group" ,
                        "consumer-1",
                        Duration.ofSeconds(10) ,
                        pending.getId()
                ) ;

                // lưu vào stream dead-letter để sưe lý sau
                for(MapRecord<String , Object ,Object> message : claimed){
                    redisTemplate.opsForStream()
                            .add(StreamRecords.mapBacked(message.getValue()).withStreamKey("voucher-dead-letter")) ;
                }
                // ACK để xóa nó khỏi PEL của voucher-stream -> voucher-group
                redisTemplate.opsForStream().acknowledge(
                        "voucher-stream",
                        "voucher-group" ,
                        pending.getId()
                );
                continue;
            }

            if(pending.getElapsedTimeSinceLastDelivery().toSeconds() >= 10){
                // lấy ra pending  thỏa mãn điều kiện idle >= 10s
                List<MapRecord<String , Object , Object>> claimed= redisTemplate.opsForStream().claim(
                        "voucher-stream" ,
                        "voucher-group",
                        "consumer-1" ,
                        Duration.ofSeconds(10) ,
                        pending.getId()
                ) ;
                // handle ở đây
                claimed.forEach(this::handleMessage);
            }

        }
    }

    // xử lý ACK\
    @Transactional
    public void handleMessage(MapRecord<String , Object , Object> message)  {
        Optional<LogOrder> exsist = logOrderRepository.findByRequestId(ParseJacksonUtil.toString(message.getValue().get("request_id").toString())) ;
        if(exsist.isPresent()){
            StatusLogOrder status =  exsist.get().getStatus() ;
            if(status == StatusLogOrder.CONFIRMED){
                redisTemplate.opsForStream().acknowledge(
                        "voucher-stream" ,
                        "voucher-group" ,
                        message.getId()
                );
                return ;
            }else if(status == StatusLogOrder.PENDING){
                processVoucher(message);
                exsist.get().setStatus(StatusLogOrder.CONFIRMED);
            }

        }else return ;

        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        System.out.println("dùng mã giảm giá thành công .");
                        redisTemplate.opsForStream().acknowledge("voucher-stream", "voucher-group" , message.getId()) ;
                    }
                }
        );
    }




    // nếu 2  consumer trở lên vẫn có thể dinh multiThread (stream cho phép )

    public void processVoucher(MapRecord<String , Object , Object> message){
        String tenDangNhap = ParseJacksonUtil.toString(message.getValue().get("username").toString()) ;
        int maGiam = Integer.parseInt(message.getValue().get("voucherID").toString()) ;

        // pessimistic_lock
        MaGiamGia maGiamGia = maGiamGiaRepository.findByMaGiamForUpdate(maGiam).orElseThrow() ;

        if(maGiamGia.getTrangThaiMaGiamGia() == TrangThaiMaGiamGia.KHOA || maGiamGia.getTrangThaiMaGiamGia() == TrangThaiMaGiamGia.HET_HAN){
            throw new VoucherStateException("mã giảm giá "+message.getValue().get("voucherID").toString()+ " không còn hoạt động") ;
        }

        Optional<MaGiamGiaNguoiDung> exsist = maGiamGiaNguoiDungRepository.findByMaGiamGia_MaGiamAndNguoiDung_TenDangNhap(maGiam,tenDangNhap);
        if(exsist.isPresent()){
            MaGiamGiaNguoiDung maGiamGiaNguoiDung = exsist.get() ;
            if(maGiamGiaNguoiDung.getDaDung() == maGiamGiaNguoiDung.getLuotDungToiDa()) return ;
            exsist.get().setDaDung(maGiamGiaNguoiDung.getDaDung() + 1);
            maGiamGiaNguoiDungRepository.save(maGiamGiaNguoiDung) ;

        }else {
            NguoiDung nguoiDung =nguoiDungRepository.findByTenDangNhap(tenDangNhap).orElseThrow() ;

            MaGiamGiaNguoiDung entity = new MaGiamGiaNguoiDung() ;
            entity.setNguoiDung(nguoiDung);
            entity.setDaDung(1); // xem lại
            entity.setMaGiamGia(maGiamGia);
            entity.setLuotDungToiDa(maGiamGia.getGioiHanSoLuongDungUser());
            entity.setNgayNhan(LocalDateTime.now());

            maGiamGiaNguoiDungRepository.save(entity) ;
        }

        if(maGiamGia.getSoMaDaDung() == maGiamGia.getSoLuong()) return ;

        maGiamGia.setSoMaDaDung(maGiamGia.getSoMaDaDung() + 1);

        // set status đơn hàng
        DonHang donHang = donHangRepository.findByRequestId(ParseJacksonUtil.toString(message.getValue().get("request_id").toString())).orElseThrow(()-> new RuntimeException("không tìm thấy sách")) ;
        donHang.setTrangThai(TrangThaiGiaoHang.DA_XAC_NHAN);

        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        System.out.println("commit DB thành công ");
                    }
                }
        );


    }

    // xử lý những mã voucher bị dead-letter
    @Scheduled(fixedDelay = 6000)
    @Transactional
    // sao 6s thì compensate
    void compensateVoucherInDeadLetter() {
        // lấy lên danh sách message(voucher cần compensate )
        List<MapRecord<String, Object, Object>> messages = redisTemplate.opsForStream().read(
                Consumer.from("voucher-dead-letter-group", "consumer-1"),
                StreamReadOptions.empty().count(10).block(Duration.ofSeconds(2)),
                StreamOffset.create("voucher-dead-letter", ReadOffset.lastConsumed())
        );
        if (messages == null || messages.isEmpty()) return;

        // lấy ra danh sách id voucher để check dưới DB rồi reconcicle
        Set<Integer> idVouchers = new HashSet<>();
        for (MapRecord<String, Object, Object> message : messages) {
            redisTemplate.opsForStream().acknowledge("voucher-dead-letter", "voucher-dead-letter-group", message.getId());
            idVouchers.add(Integer.parseInt(message.getValue().get("voucherID").toString()));
        }

        List<MaGiamGia> maGiamGias = maGiamGiaRepository.findByMaGiamIn(idVouchers);
        if (maGiamGias == null || maGiamGias.isEmpty()) return;

        for (MaGiamGia maGiamGia : maGiamGias) {
            String key = "voucher:stock:"+maGiamGia.getMaGiam();
            int redisStock = Integer.parseInt(redisTemplate.opsForValue().get(key).toString());
            if(redisStock == maGiamGia.getSoLuong() - maGiamGia.getSoMaDaDung()) continue;
            else redisTemplate.opsForValue().set(key ,  maGiamGia.getSoLuong() - maGiamGia.getSoMaDaDung());
        }

        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        messages.forEach(message ->  redisTemplate.opsForStream().acknowledge("voucher-dead-letter", "voucher-dead-letter-group", message.getId()));
                    }
                }
        );
    }

    @Transactional
    public MaGiamGia getMaGiamGia(int maGiam){
        return maGiamGiaRepository.findByMaGiam(maGiam).orElseThrow();
    }
}
