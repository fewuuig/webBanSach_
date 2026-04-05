package com.example.webbansach_backend.dto.profileUser;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UpdateProfileUserDTO {
    private String hoDem ;
    private String ten ;
    private String soDienThoai ;
    private  String anhDaiDien ;
    private char gioiTinh ;
    private LocalDateTime ngaySinh ;
}
