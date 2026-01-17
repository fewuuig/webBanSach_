package com.example.webbansach_backend.service;




// có thể phát triển tiếp như : gửi email chúc mừng sinh nhật , gửi email quà tặng , ghửi em,ail thanh toán đơn hàng thành công , ...
public interface EmailService {
  void sendEmailMessage(String from , String to , String subject , String text) ;

}
