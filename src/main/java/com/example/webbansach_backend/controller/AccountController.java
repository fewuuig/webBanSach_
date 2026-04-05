package com.example.webbansach_backend.controller;

import com.example.webbansach_backend.Entity.NguoiDung;
import com.example.webbansach_backend.Entity.RefreshToken;
import com.example.webbansach_backend.dto.Logout;
import com.example.webbansach_backend.dto.RefreshTokenRequestDTO;
import com.example.webbansach_backend.dto.account.ChangePassword;
import com.example.webbansach_backend.dto.account.DisableAccountRequestDTO;
import com.example.webbansach_backend.dto.profileUser.UpdateProfileUserDTO;
import com.example.webbansach_backend.security.JwtRespone;
import com.example.webbansach_backend.security.LoginRequest;
import com.example.webbansach_backend.service.*;
import com.example.webbansach_backend.service.impl.JwtService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/tai-khoan")
public class AccountController {

    private static final Logger log = LoggerFactory.getLogger(AccountController.class);
    private static final Logger perfLogger = LoggerFactory.getLogger("PERF_LOGGER");

    @Autowired
    private AccountService accountService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private RefreshTokenService refreshTokenService;

    // ================= REGISTER =================
    @CrossOrigin(origins = "*")
    @PostMapping("/dang-ky")
    public ResponseEntity<?> dangKy(@Validated @RequestBody NguoiDung nguoiDung) {

        long start = System.currentTimeMillis();

        try {
            ResponseEntity<?> response = accountService.dangKyTaiKhoan(nguoiDung);

            long time = System.currentTimeMillis() - start;

            perfLogger.info("POST /tai-khoan/dang-ky | username={} | time={} ms",
                    nguoiDung.getTenDangNhap(), time);

            return response;

        } catch (Exception e) {
            long time = System.currentTimeMillis() - start;

            log.error("ERROR POST /tai-khoan/dang-ky | time={} ms | error={}",
                    time, e.getMessage());

            throw e;
        }
    }

    // ================= ACTIVATE =================
    @GetMapping("/kich-hoat")
    public ResponseEntity<?> kichHoatTaiKhoan(@RequestParam("email") String email,
                                              @RequestParam("ma-kich-hoat") String maKichHoat) {

        long start = System.currentTimeMillis();

        try {
            ResponseEntity<?> response = accountService.kichHoatTaiKhoan(email, maKichHoat);

            long time = System.currentTimeMillis() - start;

            perfLogger.info("GET /tai-khoan/kich-hoat | email={} | time={} ms",
                    email, time);

            return response;

        } catch (Exception e) {
            long time = System.currentTimeMillis() - start;

            log.error("ERROR GET /tai-khoan/kich-hoat | time={} ms | error={}",
                    time, e.getMessage());

            throw e;
        }
    }

    // ================= LOGIN =================
    @PostMapping("/dang-nhap")
    public ResponseEntity<?> dangNhap(@RequestBody LoginRequest loginRequest) {

        long start = System.currentTimeMillis();

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            if (authentication.isAuthenticated()) {

                NguoiDung nguoiDung = userService.findByUsername(loginRequest.getUsername());

                String accessToken = jwtService.generateToken(loginRequest.getUsername());
                String refreshToken = refreshTokenService.createToken(nguoiDung);

                long time = System.currentTimeMillis() - start;

                perfLogger.info("POST /tai-khoan/dang-nhap | username={} | time={} ms",
                        loginRequest.getUsername(), time);

                return ResponseEntity.ok(new JwtRespone(accessToken, refreshToken));
            }

        } catch (AuthenticationException e) {

            long time = System.currentTimeMillis() - start;

            log.warn("LOGIN FAIL | username={} | time={} ms",
                    loginRequest.getUsername(), time);

            return ResponseEntity.badRequest()
                    .body("Tên đăng nhập hoặc mật khẩu không chính xác");
        }

        return ResponseEntity.badRequest()
                .body("Xác thực tài khoản không thành công");
    }

    // ================= REFRESH TOKEN =================
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequestDTO request) {

        long start = System.currentTimeMillis();

        try {
            RefreshToken rt = refreshTokenService.verify(request.getRefreshToken());

            String accessToken = jwtService.generateToken(
                    rt.getNguoiDung().getTenDangNhap()
            );

            long time = System.currentTimeMillis() - start;

            perfLogger.info("POST /tai-khoan/refresh-token | user={} | time={} ms",
                    rt.getNguoiDung().getTenDangNhap(), time);

            return ResponseEntity.ok(new JwtRespone(accessToken, request.getRefreshToken()));

        } catch (Exception e) {
            long time = System.currentTimeMillis() - start;

            log.error("ERROR POST /tai-khoan/refresh-token | time={} ms | error={}",
                    time, e.getMessage());

            throw e;
        }
    }

    // ================= LOGOUT =================
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody Logout logout) {

        long start = System.currentTimeMillis();

        try {
            refreshTokenService.delete(logout.getRefreshToken());

            long time = System.currentTimeMillis() - start;

            perfLogger.info("POST /tai-khoan/logout | time={} ms", time);

            return ResponseEntity.ok("Đăng xuất thành công");

        } catch (Exception e) {
            long time = System.currentTimeMillis() - start;

            log.error("ERROR POST /tai-khoan/logout | time={} ms | error={}",
                    time, e.getMessage());

            throw e;
        }
    }

    // ================= CHECK USERNAME =================
    @GetMapping("/check-username")
    public ResponseEntity<?> checkUsername(@RequestParam("username") String username) {

        long start = System.currentTimeMillis();

        Object result = accountService.checkUsername(username);

        long time = System.currentTimeMillis() - start;

        perfLogger.info("GET /tai-khoan/check-username | username={} | time={} ms",
                username, time);

        return ResponseEntity.ok(result);
    }

    // ================= CHECK EMAIL =================
    @GetMapping("/check-email")
    public ResponseEntity<?> checkEmail(@RequestParam("email") String email) {

        long start = System.currentTimeMillis();

        Object result = accountService.checkEmail(email);

        long time = System.currentTimeMillis() - start;

        perfLogger.info("GET /tai-khoan/check-email | email={} | time={} ms",
                email, time);

        return ResponseEntity.ok(result);
    }

    // ================= DISABLE ACCOUNT =================
    @PutMapping("/disable")
    public ResponseEntity<?> disableAccount(@RequestBody DisableAccountRequestDTO disable) {

        long start = System.currentTimeMillis();

        try {
            String admin = SecurityContextHolder.getContext().getAuthentication().getName();

            accountService.disableAccount(admin, disable.getTenDangNhap());

            long time = System.currentTimeMillis() - start;

            perfLogger.info("PUT /tai-khoan/disable | admin={} | target={} | time={} ms",
                    admin, disable.getTenDangNhap(), time);

            return ResponseEntity.noContent().build();

        } catch (Exception e) {
            long time = System.currentTimeMillis() - start;

            log.error("ERROR PUT /tai-khoan/disable | time={} ms | error={}",
                    time, e.getMessage());

            throw e;
        }
    }

    @PutMapping("/change-password")
    public ResponseEntity<?> changePassWord(@RequestBody ChangePassword changePassword){
        String tenDangNhap = SecurityContextHolder.getContext().getAuthentication().getName() ;
        accountService.changePassword(tenDangNhap , changePassword);
        return ResponseEntity.ok("updated pass") ;
    }

}