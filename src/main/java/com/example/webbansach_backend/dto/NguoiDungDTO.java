package com.example.webbansach_backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


public class NguoiDungDTO {

    @NotBlank(message = "họ đệm không đc bỏ trống ")
    private  String hoDem ;
    @NotBlank(message = "Tên không đc bỏ trống ")
    private String ten ;

    @NotBlank(message = "tên đăng nhập không được bỏ chống")
    private String tenDangNhap ;

    @NotBlank(message = "mật khẩu không đc bỏ trống")
    @Size(min = 8 )
    private String matKhau ;


    @Email(message = "email không đúng đinhj dạng")
    @NotBlank(message = "email không đc bỏ trôings ")
    private String email ;

    @NotBlank(message = "so điện thoại không đc bỏ trống")
    @Size(min =10 ,  max = 10)
    private String soDienThoai ;

}
