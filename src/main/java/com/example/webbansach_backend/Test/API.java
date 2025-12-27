package com.example.webbansach_backend.Test;

import com.example.webbansach_backend.Repository.ChiTietDonHangRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class API {
    @Autowired
    private ChiTietDonHangRepository chiTietDonHangRepository ;
    @GetMapping("/")
    public void testAPI(){
        chiTietDonHangRepository.findAll() ;
    }

}
