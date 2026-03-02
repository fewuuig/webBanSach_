package com.example.webbansach_backend.Repository;

import com.example.webbansach_backend.Entity.LogOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LogOrderRepository extends JpaRepository<LogOrder , Long> {
    Boolean existsByRequestId(String requestId) ;
    Optional<LogOrder> findByRequestId(String requestId) ;
}
