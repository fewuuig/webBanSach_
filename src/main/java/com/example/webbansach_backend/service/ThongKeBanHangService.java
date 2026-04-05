package com.example.webbansach_backend.service;

import com.example.webbansach_backend.Entity.DonHang;
import com.example.webbansach_backend.dto.stats.StatLastWeekDTO;
import com.example.webbansach_backend.dto.stats.StatTodayDTO;

import java.util.List;

public interface ThongKeBanHangService {
    // sách bán trong ngày .
    // lợi nhuận tháng này
    void  onStatsToday() ;
    StatTodayDTO getStatToday() ;
    void statWhenPlaceOrder(int totalBook , DonHang donHang) ;
    List<StatLastWeekDTO> statLastWeek(int soNgay) ;
}
