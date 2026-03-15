package com.example.webbansach_backend.service;

import com.example.webbansach_backend.dto.stats.StatTodayDTO;

public interface ThongKeBanHangService {
    // sách bán trong ngày .
    // lợi nhuận tháng này
    void  onStatsToday() ;
}
