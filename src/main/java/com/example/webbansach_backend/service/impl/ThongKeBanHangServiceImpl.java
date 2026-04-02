package com.example.webbansach_backend.service.impl;

import com.example.webbansach_backend.Entity.DonHang;
import com.example.webbansach_backend.Repository.customer.StatCustomerRepository;
import com.example.webbansach_backend.dto.DonHangTrangThaiResponeDTO;
import com.example.webbansach_backend.dto.stats.StatLastWeekDTO;
import com.example.webbansach_backend.dto.stats.StatTodayDTO;
import com.example.webbansach_backend.service.EmailService;
import com.example.webbansach_backend.service.ThongKeBanHangService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class ThongKeBanHangServiceImpl implements ThongKeBanHangService {
    @Autowired
    private RedisTemplate<String , Object> redisTemplate ;
    @Autowired
    private SimpMessagingTemplate messagingTemplate ;
    @Autowired
    @Qualifier("stats")
    private DefaultRedisScript<Long> stats ;
    @Autowired
    EmailService emailService ;
    @Autowired
    private StatCustomerRepository statCustomerRepository ;
    Integer orders = null ;
    Integer books = null ;
    Double revenue = null ;
    @Override
    @Transactional
    public void  onStatsToday() {
        String key = "stats:" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        try {
             orders = Integer.parseInt(redisTemplate.opsForHash().get(key, "orders").toString());
             books = Integer.parseInt(redisTemplate.opsForHash().get(key, "books").toString());
             revenue = Double.parseDouble(redisTemplate.opsForHash().get(key, "revenue").toString());
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            throw ex;
        }

        if(orders == null || books == null || revenue == null) return ;

        StatTodayDTO statTodayDTO = new StatTodayDTO();

        statTodayDTO.setRevenue(revenue);
        statTodayDTO.setOrders(orders);
        statTodayDTO.setBooks(books);

        messagingTemplate.convertAndSend("/topic/stats" , statTodayDTO);
    }

    @Override
    public StatTodayDTO getStatToday() {
        String key = "stats:" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        try {
            orders = Integer.parseInt(redisTemplate.opsForHash().get(key, "orders").toString());
            books = Integer.parseInt(redisTemplate.opsForHash().get(key, "books").toString());
            revenue = Double.parseDouble(redisTemplate.opsForHash().get(key, "revenue").toString());
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            throw ex;
        }
        if(orders == null || books == null || revenue == null) return null;

        StatTodayDTO statTodayDTO = new StatTodayDTO();

        statTodayDTO.setRevenue(revenue);
        statTodayDTO.setOrders(orders);
        statTodayDTO.setBooks(books);
        return statTodayDTO ;
    }
    public void statWhenPlaceOrder(int totalBook , DonHang donHang){
        // thống kê doanh số bán hàng
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd") ;
        String key = "stats:"+ LocalDateTime.now().format(dateTimeFormatter) ;
        redisTemplate.execute(stats , List.of(key) , 1 , totalBook , donHang.getTongGia()) ;
    }

    // thống kê trong 1 tuần qua
    @Override
    public List<StatLastWeekDTO> statLastWeek(int soNgay){
        // cách làm : lấy lên list field doanh thu trong 1 tuần qua -> trả về client
        // sử dụng dateTime để lấy
        List<StatLastWeekDTO> revenueOnceWeek = new ArrayList<>(soNgay) ;
        for(int i =0 ; i<=1000 ; i++){
            if (revenueOnceWeek.size() == soNgay) break ;
            try {
                String date = LocalDateTime.now().minusDays(i).format(DateTimeFormatter.ofPattern("yyyy/MM/dd")).toString();
                String key ="stats:"+date ;
                Double revenue = Double.parseDouble(redisTemplate.opsForHash().get(key,"revenue").toString()) ;

                StatLastWeekDTO statLastWeekDTO = new StatLastWeekDTO() ;
                statLastWeekDTO.setDate(date);
                statLastWeekDTO.setRevenue(revenue);
                revenueOnceWeek.add(statLastWeekDTO) ;
            }catch (NullPointerException ex){
                // nexxt
            }
        }
        return revenueOnceWeek ;
    }

    // đặt lịch gửi doanh số về email của admin về doang thu trong ngày
//    @Scheduled(cron = "0 0 0 * * ?" )
    @Scheduled(cron = "0 0 0 * * ?")
    public void sendDoanhSo(){
        // lấy kleen số đơn thành công
        int soDonThanhCong = statCustomerRepository.getQuantityOrderSuccess();
        int soDonThatBai = statCustomerRepository.getQuantityOrderFail();
         List<Integer> sachDat = statCustomerRepository.getBookOrder() ;
        double revenue= statCustomerRepository.getRevenue() ;
        int idBookBestSeller = statCustomerRepository.getIdBookBestSeller();

        String listIdBook =sachDat.toString();
        String text = """
<html>
<head>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f6f8;
            padding: 20px;
        }
        .container {
            max-width: 600px;
            margin: auto;
            background: #ffffff;
            border-radius: 12px;
            padding: 20px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.1);
        }
        .title {
            text-align: center;
            font-size: 22px;
            font-weight: bold;
            margin-bottom: 20px;
            color: #333;
        }
        .card {
            display: flex;
            justify-content: space-between;
            padding: 12px 16px;
            margin-bottom: 10px;
            border-radius: 8px;
            background: #f8f9fa;
        }
        .card span {
            font-weight: 500;
            color: #555;
        }
        .value {
            font-weight: bold;
            color: #000;
        }
        .success { color: green; }
        .fail { color: red; }
        .revenue {
            font-size: 20px;
            text-align: center;
            margin-top: 20px;
            font-weight: bold;
            color: #007bff;
        }
        .footer {
            text-align: center;
            font-size: 12px;
            margin-top: 20px;
            color: #999;
        }
    </style>
</head>

<body>
    <div class="container">
        <div class="title">Báo cáo doanh số hôm nay</div>

        <div class="card">
            <span>Đơn thành công</span>
            <span class="value success"> %d</span>
        </div>

        <div class="card">
            <span>Đơn thất bại</span>
            <span class="value fail"> %d</span>
        </div>

        <div class="card">
            <span>id sách đã bán</span>
            <span class="value"> %s</span>
        </div>
         <div class="card">
            <span>số sách đã bán</span>
            <span class="value"> %d</span>
        </div>
        <div class="card">
            <span> ID sách bán chạy</span>
            <span class="value"> %d</span>
        </div>

        <div class="revenue">
            💰 Doanh thu: %,.0f VNĐ
        </div>

        <div class="footer">
            Hệ thống tự động gửi lúc 00:00 mỗi ngày 
        </div>
    </div>
</body>
</html>
""".formatted(
                soDonThanhCong,
                soDonThatBai,
                listIdBook,
                sachDat.size(),
                idBookBestSeller,
                revenue
        );
        emailService.sendEmailMessage("loog5277@gmail.com" , "maiv09700@gmail.com","thống kê doanh số" , text);
    }

}
