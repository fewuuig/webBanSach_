package com.example.webbansach_backend.controller;

import com.example.webbansach_backend.dto.AddWishLoveRequestDTO;
import com.example.webbansach_backend.service.WishLoveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping ("/wish-love")
public class DanhSachYeuThichController {
    @Autowired
    private WishLoveService wishLoveService ;
    @GetMapping("/danh-sach-yeu-thich")
    public ResponseEntity<?> getWishLoveList(){
        String tenDangNhap = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("Xem danh sách yêu thích thành công");
        return ResponseEntity.ok(wishLoveService.getWishLoveList( tenDangNhap));
    }
    @PostMapping("/add-to-wish-love")
    public ResponseEntity<?> addWishLoveList(@RequestBody AddWishLoveRequestDTO addWishLoveRequestDTO){
        String tenDangNhap = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("Thêm vào danh sách yêu thhichs thanmhf công");
        wishLoveService.addWishLoveList(tenDangNhap , addWishLoveRequestDTO.getMaSach());
        return ResponseEntity.ok("them sahs vào giỏ hàng thành cong") ;
    }
    @GetMapping("/check")
    public ResponseEntity<?> checkWishLove(@RequestParam("maSach") int maSach){
        String tenDangNhap =SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("check thành công");
        return ResponseEntity.ok( wishLoveService.check( tenDangNhap,maSach) ) ;
    }
}
