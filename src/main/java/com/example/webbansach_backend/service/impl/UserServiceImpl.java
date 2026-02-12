package com.example.webbansach_backend.service.impl;

import com.example.webbansach_backend.Entity.NguoiDung;
import com.example.webbansach_backend.Entity.Quyen;
import com.example.webbansach_backend.Repository.NguoiDungRepository;
import com.example.webbansach_backend.Repository.QuyenRepository;
import com.example.webbansach_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.text.Collator;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class  UserServiceImpl implements UserService {

    private NguoiDungRepository nguoiDungRepository ;
    private QuyenRepository quyenRepository ;
    @Autowired
    public UserServiceImpl(NguoiDungRepository nguoiDungRepository , QuyenRepository quyenRepository){
        this.nguoiDungRepository = nguoiDungRepository ;
        this.quyenRepository = quyenRepository ;
    }

    @Override
    public NguoiDung findByUsername(String tenDangNhap) {
         return nguoiDungRepository.findByTenDangNhap(tenDangNhap)
                 .orElseThrow(()->new RuntimeException("Không tìm thấy người dùng")) ;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
            NguoiDung nguoiDung = nguoiDungRepository.findByTenDangNhap(username).orElseThrow(()->new RuntimeException("Khôg tìm thấy người dùng")) ;


            if(nguoiDung == null) {
                throw new UsernameNotFoundException("User not found");
            }

            return new User(nguoiDung.getTenDangNhap() , nguoiDung.getMatKhau() , rolesToAuthorities(nguoiDung.getDanhSachQuyen()));
    }
    private Collection<? extends GrantedAuthority> rolesToAuthorities(Collection<Quyen> quyens){
        return quyens.stream().map(quyen->new SimpleGrantedAuthority(quyen.getTenQuyen())).collect(Collectors.toList()) ;
    }
}


