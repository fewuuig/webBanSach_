package com.example.webbansach_backend.service.impl;

import com.example.webbansach_backend.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender emailSender ;

    @Override
    public void sendEmailMessage(String from, String to, String subject, String text) {
        // MimeMailMessage => gủi mail có đingh kèm media
        // SimpleMailMessage => chỉ chứa nội dúng thông thường , không có đings kèm


            try {
                MimeMessage mimeMessage = emailSender.createMimeMessage() ;
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false , "UTF-8" );

                helper.setFrom(from);
                helper.setTo(to);
                helper.setSubject(subject);
                helper.setText(text , true); // false = text thường

                emailSender.send(mimeMessage);

            } catch (MessagingException e) {
                throw new RuntimeException("Gửi email thất bại", e);
            }
        }

}

