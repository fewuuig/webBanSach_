package com.example.webbansach_backend.converter;

import com.example.webbansach_backend.Entity.GioHangSach;
import com.example.webbansach_backend.dto.ViewCartDTO;
import org.springframework.stereotype.Component;

@Component
public class ViewCartConverter {
    public ViewCartDTO toViewCart(GioHangSach gioHangSach){
        ViewCartDTO viewCartBuilder = new ViewCartDTO.Builder()
                .setMaGioHangSach(gioHangSach.getMaGioHangSach())
                .setMaSach(gioHangSach.getSach().getMaSach())
                .setSoLuong(gioHangSach.getSoLuong())
                .setTenSach(gioHangSach.getSach().getTenSach())
                .setGiaBan((gioHangSach.getSach().getGiaBan()))
                .setTongGia(gioHangSach.getSoLuong() * gioHangSach.getSach().getGiaBan())
                .build() ;
        return viewCartBuilder ;
    }
}
