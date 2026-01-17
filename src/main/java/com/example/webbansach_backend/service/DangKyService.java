package com.example.webbansach_backend.service;

import com.example.webbansach_backend.Entity.NguoiDung;
import com.example.webbansach_backend.Repository.NguoiDungRepository;
import com.example.webbansach_backend.dto.ThongBaoDTO;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.ErrorResponseException;

import java.util.UUID;

@Service
@Transactional
public class DangKyService {
    @Autowired
    private NguoiDungRepository nguoiDungRepository ;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder ;

    @Autowired
    private EmailService emailService ;
    public ResponseEntity<?> dangKyTaiKhoan(NguoiDung nguoiDung){

        try {
            String endCryptPassword = passwordEncoder.encode(nguoiDung.getMatKhau());
            nguoiDung.setMatKhau(endCryptPassword);

            // gán và gửi thông tin kicns hoạt
            nguoiDung.setMaKichHoat(taoMaKichHoat());
            nguoiDung.setDaKichHoat(false);

            // lưu người dùng vào DB
            nguoiDungRepository.save(nguoiDung) ;

            System.out.println("Dăng ký thành công ");
            // gửi email kích hoạt
            guiEmailKichHoat(nguoiDung.getEmail() , nguoiDung.getMaKichHoat());

            return ResponseEntity.ok( "Dang ký tài khoản thành công") ;
        }catch (DataIntegrityViolationException ex){
             return ResponseEntity.badRequest().body("Đăng ký thất bạo do tài khoản đã được sử dụng ") ;
        }
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


}
