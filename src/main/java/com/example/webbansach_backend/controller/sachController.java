package com.example.webbansach_backend.controller;

import com.example.webbansach_backend.Entity.Sach;
import com.example.webbansach_backend.Repository.SachRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class sachController {
    @Autowired
    private SachRepository sachRepository ;
    @PostMapping("/sach")
    private ResponseEntity themSach(@RequestBody Sach sach){
        if(sach!=null){
            sachRepository.save(sach) ;
            return ResponseEntity.badRequest().body("ok") ;
        }else {
            return ResponseEntity.badRequest().body("Loi") ;
        }
    }

}
