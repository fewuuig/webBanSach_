package com.example.webbansach_backend.Repository;

import com.example.webbansach_backend.Entity.SachYeuThich;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "sach-yeu-thich")
public interface SachYeuThichRepository extends JpaRepository<SachYeuThich,Long> {

}
