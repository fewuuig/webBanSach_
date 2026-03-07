package com.example.webbansach_backend.Repository;

import com.example.webbansach_backend.Entity.NguoiDung;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.RequestParam;

import javax.swing.text.html.Option;
import java.util.Optional;
import java.util.Set;

@RepositoryRestResource(path = "nguoi-dung")
public interface NguoiDungRepository extends JpaRepository<NguoiDung,Integer> {
    boolean existsByTenDangNhap(String tenDangNhap) ;
    boolean existsByEmail(String email) ;


    NguoiDung findByEmail(String email) ;
    @Query("SELECT nd FROM NguoiDung nd " +
            "JOIN FETCH nd.danhSachQuyen " +
            "WHERE nd.tenDangNhap = :tenDangNhap")
    Optional<NguoiDung> findByTenDangNhap(@Param("tenDangNhap") String tenDangNhap) ;

    @Query("SELECT nd FROM NguoiDung nd " +
            "LEFT JOIN fetch nd.maGiamGiaNguoiDungs mggnd " +
            "LEFT JOIN fetch mggnd.maGiamGia " +
            "WHERE nd.tenDangNhap =: tenDangNhap")
    Optional<NguoiDung> findByTenDangNhapFetchMaGiamNguoiDungAndMaGiam(@Param("tenDangNhap") String tenDangNhap) ;
    @Query("""
          SELECT DISTINCT nd.tenDangNhap
          FROM NguoiDung nd
          JOIN  nd.danhSachQuyen q
          WHERE q.tenQuyen = :tenQuyen
    """)
    Set<String> findAllUsernameManager(@Param("tenQuyen") String tenQuyen) ;
}
