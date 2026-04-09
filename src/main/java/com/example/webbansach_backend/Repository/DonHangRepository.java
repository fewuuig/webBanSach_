package com.example.webbansach_backend.Repository;

import com.example.webbansach_backend.Entity.DonHang;
import com.example.webbansach_backend.Entity.NguoiDung;
import com.example.webbansach_backend.Enum.TrangThaiGiaoHang;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RepositoryRestResource(path = "don-hang")
public interface DonHangRepository extends JpaRepository<DonHang,Integer> {
    List<DonHang> findByTrangThai(TrangThaiGiaoHang trangThaiGiaoHang);
//    @EntityGraph(attributePaths = "danhSachChiTietDonHang")
//    @Query(value = """
//          SELECT dh.*
//          FROM don_hang dh
//          JOIN nguoi_dung nd ON nd.ma_nguoi_dung = dh.ma_nguoi_dung
//          WHERE nd.ten_dang_nhap = :tenDangNhap and dh.trang_thai = :trangThaiGiaoHang
//
//
//    """ , nativeQuery = true)

    @EntityGraph(attributePaths = {
            "danhSachChiTietDonHang",
            "danhSachChiTietDonHang.sach"
    })
    @Query("""
        SELECT dh
        FROM DonHang dh
        WHERE dh.nguoiDung = :nguoiDung and dh.trangThai = :trangThaiGiaoHang
    """)

    List<DonHang> findByNguoiDungAndTrangThai(@Param("nguoiDung") NguoiDung nguoiDung ,@Param("trangThaiGiaoHang") TrangThaiGiaoHang trangThaiGiaoHang) ;
    Optional<DonHang> findByNguoiDung_TenDangNhapAndMaDonHang(String tenDangNhap , int maDonHang) ;
    List<DonHang> findByRequestIdIn(Set<String> requestId) ;

    @EntityGraph(attributePaths = "danhSachChiTietDonHang") // lâyus hết danh sacxhs chi tiết đơn hàng lên
    @Query( """
          SELECT  dh FROM DonHang  dh
          WHERE dh.ngayTao >= :startDate And dh.ngayTao <:endDate
     """)
    List<DonHang> findByOrderFailTwoDay(@Param("startDate") LocalDateTime startDate, @Param("endDate")LocalDateTime endDate) ;

    @Modifying // dùng cho insert , update ,delete
    @Query(value = """
         UPDATE don_hang dh
         SET dh.trang_thai = 'DA_HUY'
         WHERE dh.ma_don_hang IN :orderIds
     """ , nativeQuery = true  )
    void updateStateOrderTimeout(@Param("orderIds") List<Object> orderIds) ;

    @Modifying
    @Query(value = """
          UPDATE sach s
          JOIN chi_tiet_don_hang ctdh
          ON ctdh.ma_sach = s.ma_sach
          SET s.so_luong = s.so_luong + ctdh.so_luong
          WHERE ma_don_hang IN :orderIds
    """ , nativeQuery = true)
    void returnOrderTimeoutIntoStock(@Param("orderIds") List<Object> orderIds ) ;
}





//raph quanr lý ralation cực tốt -> tối ưu
