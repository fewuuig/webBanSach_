package com.example.webbansach_backend.controller;

import com.example.webbansach_backend.Entity.NguoiDung;
import com.example.webbansach_backend.security.JwtRespone;
import com.example.webbansach_backend.security.LoginRequest;
import com.example.webbansach_backend.service.DangKyService;
import com.example.webbansach_backend.service.UserService;

import com.example.webbansach_backend.service.impl.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tai-khoan")
public class TaiKhoanController {
    @Autowired
    private DangKyService dangKyService ;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService ;
    @Autowired
    private JwtService jwtService ;
    @CrossOrigin(origins = "*")
    @PostMapping("/dang-ky")
    private ResponseEntity<?> dangKy(@Validated @RequestBody NguoiDung nguoiDung){
        ResponseEntity<?> response = dangKyService.dangKyTaiKhoan(nguoiDung) ;

        return response ;
    }
    @GetMapping(value = "/kich-hoat")
    private ResponseEntity<?> kichHoatTaiKhoan(@RequestParam("email") String email,@RequestParam("ma-kich-hoat") String maKichHoat){
        ResponseEntity<?> response =  dangKyService.kichHoatTaiKhoan(email , maKichHoat);
        return response ;
    }

    @PostMapping("/dang-nhap")
    public ResponseEntity<?>  dangNhap(@RequestBody LoginRequest loginRequest){
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername() , loginRequest.getPassword())
            ) ;
            System.out.println("xác thưc thành công");
            // nếu xác thưicj tài khoản thamhf cồn thì tạo token
            if(authentication.isAuthenticated()){
                 final String jwt =  jwtService.generateToken(loginRequest.getUsername()) ;
                System.out.println("xác thưc thành công");
                 return ResponseEntity.ok(new JwtRespone(jwt)) ;

            }

        }catch (AuthenticationException e){
            // nếu không xác thực thành công
            return ResponseEntity.badRequest().body("Tên đăng nhập hoặc mật khẩu không chính xác") ;
        }
        return ResponseEntity.badRequest().body("Xác thưcj tài khoản không thành công") ;
    }
}
