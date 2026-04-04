package com.example.webbansach_backend.service.impl;

import com.example.webbansach_backend.Entity.NguoiDung;
import com.example.webbansach_backend.Entity.NguoiDungQuyen;
import com.example.webbansach_backend.Entity.Quyen;
import com.example.webbansach_backend.Enum.CheckAccount;
import com.example.webbansach_backend.Repository.NguoiDungRepository;
import com.example.webbansach_backend.Repository.QuyenRepository;
import com.example.webbansach_backend.exception.DuplicationDisableException;
import com.example.webbansach_backend.service.AccountService;
import com.example.webbansach_backend.service.EmailService;
import com.example.webbansach_backend.utils.CheckRoleUItil;
import com.example.webbansach_backend.utils.ParseJacksonUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;
import java.util.UUID;

@Service
public class AccountServiceImpl implements AccountService {
    @Autowired
    private RedisTemplate<String , Object> redisTemplate ;
    @Autowired
    private NguoiDungRepository nguoiDungRepository ;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder ;

    @Autowired
    private EmailService emailService ;
    @Autowired
    private QuyenRepository quyenRepository ;

    // check ussername
    private boolean checkAccount(CheckAccount checkAccount, String target){
        String cache = null ;
        try {
            if(checkAccount == CheckAccount.USERNAME) {

                cache = redisTemplate.opsForValue().get("username:" + target).toString();
            }
            else if(checkAccount == CheckAccount.EMAIL) cache = redisTemplate.opsForValue().get("email:"+target).toString() ;
        }catch (Exception ex){
            return false ;
        }
        if(cache.equals(target)) return true ;
        return false ;
    }


    // check ussernmae cos toonf taij khoong
    public boolean checkUsername(String username){
        return checkAccount(CheckAccount.USERNAME , username) ;
    }

    // check email khi đăng ký tài khoản
    public boolean checkEmail(String email){
        return checkAccount(CheckAccount.EMAIL , email) ;
    }
    @Transactional
    public ResponseEntity<?> dangKyTaiKhoan(NguoiDung nguoiDung){

        boolean exists = nguoiDungRepository.existsByTenDangNhap(nguoiDung.getTenDangNhap())  ;
        if(exists) throw new RuntimeException("tài khoản đã tồn tại") ;
            String endCryptPassword = passwordEncoder.encode(nguoiDung.getMatKhau());
            nguoiDung.setMatKhau(endCryptPassword);

            // gán và gửi thông tin kicns hoạt
            nguoiDung.setMaKichHoat(taoMaKichHoat());
            nguoiDung.setDaKichHoat(false);

            // tạo quyền
            Quyen quyen =  quyenRepository.findByTenQuyen("USER") ;
            NguoiDungQuyen nguoiDungQuyen = new NguoiDungQuyen() ;
            nguoiDungQuyen.setQuyen(quyen);
            nguoiDungQuyen.setNguoiDung(nguoiDung);

            nguoiDung.getNguoiDungQuyens().add(nguoiDungQuyen);
            // lưu người dùng vào DB
            nguoiDungRepository.save(nguoiDung) ;

            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    redisTemplate.opsForValue().set("username:"+nguoiDung.getTenDangNhap() , nguoiDung.getTenDangNhap());
                    // gửi email kích hoạt
                    guiEmailKichHoat(nguoiDung.getEmail() , nguoiDung.getMaKichHoat());
                }
            });
            return ResponseEntity.ok("đăng ký thành công") ;
    }

    public ResponseEntity<?> kichHoatTaiKhoan(String email , String maKichHoat ){
        try {
            NguoiDung nguoiDung = nguoiDungRepository.findByEmail(email) ;
            if(nguoiDung == null){
                return ResponseEntity.badRequest().body("Đăng ký thất bại , tài khoản không tồn tại") ;
            }
            if(nguoiDung.getEmail() == null){
                return ResponseEntity.badRequest().body("Email không toòn tại");
            }
            if(nguoiDung.getDaKiHoat()){
                return ResponseEntity.badRequest().body("tài khoản đã được kích hoạt rồi . không cần thực hiện lại");
            }
            if(nguoiDung.getMaKichHoat().equals(maKichHoat)){
                nguoiDung.setDaKichHoat(true) ;
                nguoiDungRepository.save(nguoiDung) ;
                return ResponseEntity.ok("Đăng kí thành công")  ;
            }else {
                return ResponseEntity.badRequest().body("mã sai");
            }
        }catch(Exception ex){
            return ResponseEntity.badRequest().body("Lỗi xử lý") ;
        }
    }


    // tao 1 đonaj mã ngẫu nhiên
    private String taoMaKichHoat(){
        return UUID.randomUUID().toString() ;
    }

    private void guiEmailKichHoat(String email , String maKichHoat){
        String url = "http://localhost:3000/kich-hoat/"+email+"/"+maKichHoat ;
        String subject = "Mã kích hoạt tài khoản của bạn tại trang WEB webbansach.com" ;
        String text = "Nhap ma sau để kích hoạt cho tài khoản "+email+"<html><body><br/></body></html>" + maKichHoat ;
        text += "<br/>" +"Bấm vào đây để kích hoạt taì khoản " +"<a href="+url+">"+url+"</a>" ;
        emailService.sendEmailMessage("loog5277@gmail.com" , email , subject , text);
    }

    @Transactional
    public void disableAccount(String tenDangNhap , String tenDangNhapofUser){
        NguoiDung nguoiDungDisable = nguoiDungRepository.findByTenDangNhap(tenDangNhapofUser).
                orElseThrow(()->new RuntimeException("Not find:"+tenDangNhapofUser)) ;
        if(!nguoiDungDisable.getDaKiHoat())
            throw new  DuplicationDisableException("tài khoản này đã bị vô hiệu hóa rồi . không thể thực hiện lại");

        NguoiDung nguoiDung = nguoiDungRepository.findByTenDangNhap(tenDangNhap).orElseThrow() ;
        if(CheckRoleUItil.checkRole(nguoiDung)){
            nguoiDungDisable.setDaKichHoat(false);
        }else throw new RuntimeException("NGuoi dung k đủ quyền đẻ vô hiệu hóa tài khoản") ;
    }

}
