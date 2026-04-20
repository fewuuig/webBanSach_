package com.example.webbansach_backend.Repository;

import com.example.webbansach_backend.Entity.DanhGia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "danh-gia")
public interface DanhGiaRepository extends JpaRepository<DanhGia,Long> {
//    Page<DanhGia> findByNguoiDung_MaNguoiD(@RequestParam("maNguoiDung"))
}
