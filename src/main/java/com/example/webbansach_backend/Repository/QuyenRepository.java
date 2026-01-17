package com.example.webbansach_backend.Repository;

import com.example.webbansach_backend.Entity.Quyen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "quyen")
public interface QuyenRepository extends JpaRepository<Quyen,Integer> {
    Quyen findByTenQuyen(String quyen) ;
}
