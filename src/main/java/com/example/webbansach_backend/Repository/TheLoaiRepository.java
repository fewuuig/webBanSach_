package com.example.webbansach_backend.Repository;

import com.example.webbansach_backend.Entity.TheLoai;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

public interface TheLoaiRepository extends JpaRepository<TheLoai,Integer> {
    @EntityGraph( attributePaths="danhSachQuyenSach")
    @Query("""
          SELECT tl
          FROM TheLoai tl
     """ )
    List<TheLoai> findByTheLoai() ;
}
