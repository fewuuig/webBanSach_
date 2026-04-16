package com.example.webbansach_backend.controller;

import com.example.webbansach_backend.service.ThongKeBanHangService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/stats")
public class StatController {

    private static final Logger log = LoggerFactory.getLogger(StatController.class);
    private static final Logger perfLogger = LoggerFactory.getLogger("PERF_LOGGER");

    @Autowired
    private ThongKeBanHangService thongKeBanHangService;

    @GetMapping("/stat-today")
    public ResponseEntity<?> statToday() {

        long start = System.currentTimeMillis();

        try {
            Object result = thongKeBanHangService.getStatToday();

            long time = System.currentTimeMillis() - start;

            perfLogger.info("GET /stats/stat-today | time={} ms", time);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            long time = System.currentTimeMillis() - start;

            log.error("ERROR GET /stats/stat-today | time={} ms | error={}",
                    time, e.getMessage());

            throw e;
        }
    }
    @GetMapping("/once-week/{soNgay}")
    public ResponseEntity<?> getStatOnceWeek(@PathVariable("soNgay") int soNgay){
        return ResponseEntity.ok(thongKeBanHangService.statLastWeek(soNgay)) ;
    }

}