package com.example.webbansach_backend.service;

import com.example.webbansach_backend.Entity.DonHang;
import com.example.webbansach_backend.dto.stats.StatTodayDTO;

public interface ThongKeBanHangService {
    // sách bán trong ngày .
    // lợi nhuận tháng này
    void  onStatsToday() ;
    StatTodayDTO getStatToday() ;
    void statWhenPlaceOrder(int totalBook , DonHang donHang) ;
}
