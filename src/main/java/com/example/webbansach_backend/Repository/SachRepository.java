package com.example.webbansach_backend.Repository;

import com.example.webbansach_backend.Entity.Sach;
import jakarta.persistence.LockModeType;
import lombok.Builder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RepositoryRestResource(path = "sach")
public interface SachRepository extends JpaRepository<Sach,Integer> {
     Page<Sach> findByTenSachContaining(@RequestParam("tenSach") String tenSach , Pageable pageable) ;
     Page<Sach> findByDanhSachTheLoai_MaTheLoai(@RequestParam("maTheLoai") int maTheLoai , Pageable pageable) ;
     Page<Sach> findByTenSachContainingAndDanhSachTheLoai_MaTheLoai(@RequestParam("tenSach") String tenSach ,@RequestParam("maTheLoai") int maTheLoai ,Pageable pageable) ;
     Optional<Sach> findByMaSachAndIsActive(int maSach , boolean isActive ) ;
     Page<Sach> findAll(Pageable pageable) ;
     List<Sach> findByMaSachInAndIsActive(Collection<Integer> danhSachMaSach ,  boolean isActive) ;
     @Query(value = """
          SELECT s.*
          FROM sach s
          JOIN sach_theloai stl ON stl.ma_sach = s.ma_sach
          WHERE s.ma_sach in :maSaches And s.is_active = true and stl.ma_the_loai = :maTheLoai
    """ , nativeQuery = true)
     List<Sach> findByMaSachInAndIsActiveAndMaTheLoai(@Param("maSaches") Collection<Integer> danhSachMaSach ,@Param("maTheLoai") int maTheLoai) ;

     @Lock(LockModeType.PESSIMISTIC_WRITE)
     @Query("select s from Sach s where s.maSach = :maSach")
     Optional<Sach> findByIdForUpdate(@Param("maSach") int maSach) ;

     @Query("SELECT DISTINCT s FROM Sach s " +
             "LEFT JOIN FETCH s.maGiamGiaSaches " +
             "WHERE s.maSach IN :danhSachMaSach ")
     List<Sach> findByMaSachInFetch(@Param("danhSachMaSach") List<Integer> danhSachMaSach) ;
     boolean existsByIsbn(String isbn) ;
     boolean existsByMaSach(int maSach) ;
     @Modifying
     @Query(value = """
                  UPDATE sach
                  SET sach.is_active = false
                  WHERE sach.ma_sach IN :ids
             """,nativeQuery = true)
     void updateIsActive(@Param("ids") List<Integer> ids) ;

     // lấy 3 quyển cho carousel
     @Query(value = """
          SELECT s.*
          FROM sach s
          WHERE s.is_active = true
          ORDER BY s.ma_sach desc
          LIMIT 3
     """ , nativeQuery = true)
     List<Sach>  findSachNew() ;

     @Query(value = """
          SELECT s.*
          FROM sach s
          JOIN sach_theloai stl ON stl.ma_sach =s.ma_sach
          WHERE s.is_active = false and stl.ma_the_loai = :maTheLoai
     """,nativeQuery = true)
     List<Sach> findSachDeleted(@Param("maTheLoai") int maTheLoai) ;

     @Modifying
     @Query(value = """
          UPDATE sach
          SET sach.is_active = true
          WHERE sach.ma_sach IN :ids
     """,nativeQuery = true)
     void reStoreBook(@Param("ids") List<Integer> ids) ;
}
