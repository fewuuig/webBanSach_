package com.example.webbansach_backend.controller;

import com.example.webbansach_backend.Entity.NguoiDung;
import com.example.webbansach_backend.Entity.RefreshToken;
import com.example.webbansach_backend.Repository.RefreshTokenRepository;
import com.example.webbansach_backend.dto.Logout;
import com.example.webbansach_backend.dto.RefreshTokenRequestDTO;
import com.example.webbansach_backend.security.JwtRespone;
import com.example.webbansach_backend.security.LoginRequest;
import com.example.webbansach_backend.service.DangKyService;
import com.example.webbansach_backend.service.NguoiDungService;
import com.example.webbansach_backend.service.RefreshTokenService;
import com.example.webbansach_backend.service.UserService;

import com.example.webbansach_backend.service.impl.JwtService;
import com.example.webbansach_backend.service.impl.RefreshTokenServiceImpl;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
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
    @Autowired
    private RefreshTokenService refreshTokenService ;
    @Autowired
    private NguoiDungService nguoiDungService ;
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
                NguoiDung nguoiDung = userService.findByUsername(loginRequest.getUsername()) ;

                // taọ accessToken
                String accessToken = jwtService.generateToken(loginRequest.getUsername());
                String refreshToken = refreshTokenService.createToken(nguoiDung) ;
                return ResponseEntity.ok(new JwtRespone(accessToken , refreshToken)) ;
            }

        }catch (AuthenticationException e){
            // nếu không xác thực thành công
            return ResponseEntity.badRequest().body("Tên đăng nhập hoặc mật khẩu không chính xác") ;
        }
        return ResponseEntity.badRequest().body("Xác thưcj tài khoản không thành công") ;
    }
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequestDTO refreshTokenRequestDTO){
        RefreshToken rt = refreshTokenService.verify(refreshTokenRequestDTO.getRefreshToken()) ;
        String accessToken = jwtService.generateToken(rt.getNguoiDung().getTenDangNhap());
        System.out.println("Refresh token thành công");
        return ResponseEntity.ok(new JwtRespone(accessToken , refreshTokenRequestDTO.getRefreshToken())) ;
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody Logout logout){
        refreshTokenService.delete(logout.getRefreshToken());
        System.out.println("Đăng xuất thành công");
        return ResponseEntity.ok("Đăng xuất thành công") ;
    }
    @GetMapping("/lay-thong-tin")
    public ResponseEntity<?> getThongTintaiKhoan(){
        String tenDangNhap = SecurityContextHolder.getContext().getAuthentication().getName() ;
        return ResponseEntity.ok(nguoiDungService.getThongTin(tenDangNhap)) ;
    }
}
