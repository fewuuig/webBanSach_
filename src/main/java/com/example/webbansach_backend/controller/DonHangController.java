package com.example.webbansach_backend.controller;

import com.example.webbansach_backend.Enum.TrangThaiGiaoHang;
import com.example.webbansach_backend.dto.TrangThaiDonHangRequestDTO;
import com.example.webbansach_backend.service.impl.OrderServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

// cập nhật trạng thái của đơn hàng
@RestController
@RequestMapping("/don-hang")
public class DonHangController {
    @Autowired
    private OrderServiceImpl orderService ;

    @PutMapping("/{id}/trang-thai")
    public ResponseEntity<?> capNhatTrangThaiGiaoHang(@PathVariable int id, @RequestBody TrangThaiDonHangRequestDTO capNhatTrangThaiRequestDTO){
        orderService.capNhatTrangThaiDonHang(id , capNhatTrangThaiRequestDTO.getTrangThai());
        return ResponseEntity.noContent().build(); // trả về 204 update k cần trả về dữ liệu (status : 204) - success
    }
    @GetMapping("/trang-thai")
    public ResponseEntity<?> getDonHangTheoTrangThai(@RequestParam(value = "trangThai")TrangThaiGiaoHang trangThaiGiaoHang){
        String tenDangNhap = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(orderService.getDonHangTheoTrangThai( tenDangNhap,trangThaiGiaoHang)) ;
    }
    @PutMapping("/{maDonHang}/huy-don")
    public ResponseEntity<?> huyDonDatHang(@PathVariable("maDonHang") int maDonHang){
        String tenDangNhap = SecurityContextHolder.getContext().getAuthentication().getName();
        orderService.thaoTacDonHang(tenDangNhap ,maDonHang );
        return ResponseEntity.noContent().build() ;
    }
    @PutMapping("/{maDonHang}/dat-lai")
    public ResponseEntity<?> datLaiDonDatHang(@PathVariable("maDonHang") int maDonHang){
        String tenDangNhap = SecurityContextHolder.getContext().getAuthentication().getName();
        orderService.thaoTacDonHang(tenDangNhap ,maDonHang );
        return ResponseEntity.noContent().build() ;
    }
}
