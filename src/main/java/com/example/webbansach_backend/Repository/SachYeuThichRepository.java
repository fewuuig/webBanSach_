package com.example.webbansach_backend.Repository;

import com.example.webbansach_backend.Entity.NguoiDung;
import com.example.webbansach_backend.Entity.Sach;
import com.example.webbansach_backend.Entity.SachYeuThich;
import com.example.webbansach_backend.dto.WishLoveDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;
import java.util.Optional;

@RepositoryRestResource(path = "sach-yeu-thich")
public interface SachYeuThichRepository extends JpaRepository<SachYeuThich,Long> {
    Optional<?> findByNguoiDungAndSach(NguoiDung nguoiDung , Sach sach) ;
    Optional<SachYeuThich> findByNguoiDung_TenDangNhapAndSach_MaSach(String tenDangNhap , int maSach) ;
    List<WishLoveDTO> findByNguoiDung_TenDangNhap(String tenDangNhap) ;

}
