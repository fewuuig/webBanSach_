package com.example.webbansach_backend.Repository;

import com.example.webbansach_backend.Entity.LogOrder;
import org.hibernate.validator.internal.util.logging.Log;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface LogOrderRepository extends JpaRepository<LogOrder , Long> {
    Boolean existsByRequestId(String requestId) ;
    List<LogOrder> findByRequestIdIn(Set<String> requestId) ;
}
