package com.example.webbansach_backend.Entity;

import com.example.webbansach_backend.Enum.StatusLogOrder;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "log_order")
@Data
public class LogOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_order_id")
    private Long logOrderId ;

    @Column(name = "voucher_id")
    private int voucherID ;

    @Column(name = "request_id")
    private String requestId ;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private StatusLogOrder status ;

    @Column(name = "create_at")
    private LocalDateTime createAt ;

}
