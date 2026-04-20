package com.example.webbansach_backend.Repository;

import com.example.webbansach_backend.Entity.MaGiamGia;
import com.example.webbansach_backend.Entity.NguoiDung;
import com.example.webbansach_backend.Enum.DoiTuongApDungMa;
import com.example.webbansach_backend.Enum.TrangThaiMaGiamGia;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface MaGiamGiaRepository extends JpaRepository<MaGiamGia,Integer> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT mgg FROM MaGiamGia mgg " +
            "WHERE mgg.maGiam = :maGiam")
    Optional<MaGiamGia> findByMaGiamForUpdate(@Param("maGiam") int maGiam) ;

    @Query("SELECT mgg FROM MaGiamGia mgg " +
            "WHERE mgg.trangThaiMaGiamGia = :trangThaiMaGiamGia " +
            "AND mgg.ngayHetHan <= :now")
    List<MaGiamGia> findByTrangThaiHoatDongAndNgayHetHan(@Param("trangThaiMaGiamGia") TrangThaiMaGiamGia trangThaiMaGiamGia,
                                                         @Param("now")LocalDateTime now) ;
    Optional<MaGiamGia> findByMaGiam(int maGiam) ;
    @Query("SELECT mgg FROM MaGiamGia mgg " +
            "WHERE mgg.doiTuongApDungMa = :doiTuongApDungMa " +
            "AND mgg.trangThaiMaGiamGia = :trangThaiMaGiamGia")
    List<MaGiamGia> findByDoiTuongApDungMaANdTrangThaiHoatDongForFetch(DoiTuongApDungMa doiTuongApDungMa ,TrangThaiMaGiamGia trangThaiMaGiamGia ) ;

    List<MaGiamGia> findByTrangThaiMaGiamGia(TrangThaiMaGiamGia trangThaiMaGiamGia) ;
    List<MaGiamGia> findByTrangThaiMaGiamGiaAndDoiTuongApDungMa(TrangThaiMaGiamGia trangThaiMaGiamGia , DoiTuongApDungMa doiTuongApDungMa) ;

    // lọc mã từ DB để tránh load lên quá nhiều
    @Query("""
    SELECT mgg from MaGiamGia  mgg
    WHERE mgg.trangThaiMaGiamGia = :trangThaiHoatDong AND mgg.doiTuongApDungMa = :doiTuongApDung
    AND mgg.maGiam NOT IN(
         SELECT mggnd.maGiamGia.maGiam from MaGiamGiaNguoiDung mggnd
         WHERE mggnd.nguoiDung = :nguoiDung and mggnd.daDung >= mggnd.luotDungToiDa
    )
    """)
    List<MaGiamGia> findMaGiamGiaActive(@Param("trangThaiHoatDong") TrangThaiMaGiamGia trangThaiHoatDong ,
                                        @Param("doiTuongApDung") DoiTuongApDungMa doiTuongApDung ,
                                        @Param("nguoiDung") NguoiDung nguoiDung) ;
    List<MaGiamGia> findByMaGiamIn(List<Integer> ids ) ;
    List<MaGiamGia> findByMaGiamIn(Set<Integer> ids ) ;
}
