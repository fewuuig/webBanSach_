package com.example.webbansach_backend.Repository;

import com.example.webbansach_backend.Entity.ChiTietDonHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path ="chi-tiet-don-hang")
public interface ChiTietDonHangRepository extends JpaRepository<ChiTietDonHang,Long> {

}
