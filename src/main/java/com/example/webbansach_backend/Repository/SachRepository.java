package com.example.webbansach_backend.Repository;

import com.example.webbansach_backend.Entity.Sach;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@RepositoryRestResource(path = "sach")
public interface SachRepository extends JpaRepository<Sach,Integer> {
     Page<Sach> findByTenSachContaining(@RequestParam("tenSach") String tenSach , Pageable pageable) ;
     Page<Sach> findByDanhSachTheLoai_MaTheLoai(@RequestParam("maTheLoai") int maTheLoai , Pageable pageable) ;
     Page<Sach> findByTenSachContainingAndDanhSachTheLoai_MaTheLoai(@RequestParam("tenSach") String tenSach ,@RequestParam("maTheLoai") int maTheLoai ,Pageable pageable) ;
     Optional<Sach> findByMaSach(int maSach ) ;
     List<Sach> findByMaSachIn(List<Integer> danhSachMaSach) ;

     @Lock(LockModeType.PESSIMISTIC_WRITE)
     @Query("select s from Sach s where s.maSach = :maSach")
     Optional<Sach> findByIdForUpdate(@Param("maSach") int maSach) ;

     @Query("SELECT DISTINCT s FROM Sach s " +
             "LEFT JOIN FETCH s.maGiamGiaSaches " +
             "WHERE s.maSach IN :danhSachMaSach ")
     List<Sach> findByMaSachInFetch(@Param("danhSachMaSach") List<Integer> danhSachMaSach) ;
}
