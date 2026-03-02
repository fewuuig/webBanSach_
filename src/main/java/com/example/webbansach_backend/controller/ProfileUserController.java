package com.example.webbansach_backend.controller;

import com.example.webbansach_backend.service.ProfileUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/profile")
public class ProfileUserController {
    @Autowired
    private ProfileUserService profileUserService ;

    @GetMapping("/info")
    public ResponseEntity<?> getProfileUser(){
        String tenDangNhap = SecurityContextHolder.getContext().getAuthentication().getName() ;
        return ResponseEntity.ok(profileUserService.getProfileUser(tenDangNhap)) ;
    }
}
