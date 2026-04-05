package com.example.webbansach_backend.controller;

import com.example.webbansach_backend.dto.profileUser.UpdateProfileUserDTO;
import com.example.webbansach_backend.service.ProfileUserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/profile")
public class ProfileUserController {

    private static final Logger log = LoggerFactory.getLogger(ProfileUserController.class);
    private static final Logger perfLogger = LoggerFactory.getLogger("PERF_LOGGER");

    @Autowired
    private ProfileUserService profileUserService;

    // ================= MY PROFILE =================
    @GetMapping("/info")
    public ResponseEntity<?> getProfileUser() {

        long start = System.currentTimeMillis();

        try {
            String tenDangNhap = SecurityContextHolder.getContext().getAuthentication().getName();

            Object result = profileUserService.getProfileUser(tenDangNhap);

            long time = System.currentTimeMillis() - start;

            perfLogger.info("GET /profile/info | user={} | time={} ms",
                    tenDangNhap, time);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            long time = System.currentTimeMillis() - start;

            log.error("ERROR GET /profile/info | time={} ms | error={}",
                    time, e.getMessage());

            throw e;
        }
    }

    // ================= OTHER USER =================
    @GetMapping("/info/user-other")
    public ResponseEntity<?> getProfileOfUserOther(@RequestParam("userOther") String userOther) {

        long start = System.currentTimeMillis();

        try {
            Object result = profileUserService.getProfileUser(userOther);

            long time = System.currentTimeMillis() - start;

            perfLogger.info("GET /profile/info/user-other | targetUser={} | time={} ms",
                    userOther, time);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            long time = System.currentTimeMillis() - start;

            log.error("ERROR GET /profile/info/user-other | time={} ms | error={}",
                    time, e.getMessage());

            throw e;
        }
    }
    @PutMapping("/update")
    public ResponseEntity<?> updateProfile(@RequestBody UpdateProfileUserDTO updateProfileUserDTO){
        System.out.println("ngày sinh là : " + updateProfileUserDTO.getNgaySinh());
        String tenDangNhap = SecurityContextHolder.getContext().getAuthentication().getName() ;
        profileUserService.updateProfileUser(tenDangNhap , updateProfileUserDTO);
        return ResponseEntity.noContent().build() ;
    }
}