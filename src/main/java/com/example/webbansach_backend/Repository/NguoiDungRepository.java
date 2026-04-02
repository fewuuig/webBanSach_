package com.example.webbansach_backend.Repository;

import com.example.webbansach_backend.Entity.NguoiDung;
import com.example.webbansach_backend.dto.account.Acccount;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.RequestParam;
import org.w3c.dom.stylesheets.LinkStyle;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RepositoryRestResource(path = "nguoi-dung")
public interface NguoiDungRepository extends JpaRepository<NguoiDung,Integer> {
    boolean existsByTenDangNhap(String tenDangNhap) ;
    boolean existsByEmail(String email) ;


    NguoiDung findByEmail(String email) ;

    @EntityGraph(attributePaths = "diaChiGiaoHangs")
    @Query("""
        SELECT nd
        FROM NguoiDung nd
        WHERE nd.tenDangNhap = :tenDangNhap
    """)
    Optional<NguoiDung> findByTenDangNhapFetchDiaChi(@Param("tenDangNhap") String tenDangNhap) ;
    Optional<NguoiDung> findByTenDangNhap(@Param("tenDangNhap") String tenDangNhap) ;

    @Query("SELECT nd FROM NguoiDung nd " +
            "LEFT JOIN fetch nd.maGiamGiaNguoiDungs mggnd " +
            "LEFT JOIN fetch mggnd.maGiamGia " +
            "WHERE nd.tenDangNhap =: tenDangNhap")
    Optional<NguoiDung> findByTenDangNhapFetchMaGiamNguoiDungAndMaGiam(@Param("tenDangNhap") String tenDangNhap) ;

    @Query("""
        SELECT nd.email
        FROM NguoiDung nd
    """)
    List<String> findAllEmail() ;
    @Query("""
        SELECT nd.tenDangNhap
        FROM NguoiDung nd
    """)
    List<String> findAllUsername() ;
    boolean existsByTenDangNhapIn(List<String> tenDangNhap) ;

}
