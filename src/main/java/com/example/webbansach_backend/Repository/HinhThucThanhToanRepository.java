package com.example.webbansach_backend.Repository;

import com.example.webbansach_backend.Entity.HinhThucThanhToan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;
import java.util.Optional;

@RepositoryRestResource(path = "hinh-thuc-thanh-toan")
public interface HinhThucThanhToanRepository extends JpaRepository<HinhThucThanhToan,Integer> {
    Optional<HinhThucThanhToan> findById(int maHinhThucThanhToan) ;
    List<HinhThucThanhToan> findByMaHinhThucThanhToanIn(List<Integer> danhSachMaHinhThucThanhToan) ;
}