package com.example.webbansach_backend.service.impl;

import com.example.webbansach_backend.service.ExecuterService;
import com.example.webbansach_backend.service.OrderService;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Component
public class ExecuterServiceImpl implements ApplicationRunner , DisposableBean , ExecuterService {

    @Autowired
    private OrderService orderService ;
    private ExecutorService executerService ;
    @Autowired
    private RedisTemplate<String , Object> redisTemplate ;
    private boolean isRunning = true ;
    @Override
    public void run(ApplicationArguments args) throws Exception {
          executerService = Executors.newFixedThreadPool(8) ;

          // mở 8 thread
        for(int i =0 ; i<8 ;i++ ){
            int finalI = i;
            executerService.submit(()->{
                int shard = finalI;
                taskConsumeMessage(shard) ;
            });
        }

    }
    @Override
    public  void taskConsumeMessage(int shard){
        while (isRunning && !Thread.currentThread().isInterrupted()){
            // đọc consumer tại đây
            try { // nhiệm vụ cuiar vòng này k cho nó ngắt kết nối
                List<MapRecord<String , Object , Object>> messages = redisTemplate.opsForStream().read(
                        Consumer.from("group:shard-"+shard , "consumer-1") ,
                        StreamReadOptions.empty().count(300).block(Duration.ofSeconds(2)) ,
                        StreamOffset.create("order-stream:{ws}:shard-"+shard , ReadOffset.lastConsumed())
                );
                if(messages == null ||  messages.isEmpty()  ) continue;
                try {
                    // lưu batch
                    orderService.saveBatch(messages ,shard);
                }catch (Exception ex){
                    System.out.println("Quá trình save bath message bị lỗi");
                }
            }catch (Exception ex){
                System.out.println("Lỗi redis/kết nối/bộ nhớ ....");
                try {
                    Thread.sleep(3000); // lỗi thì ngủ 1 tý rồi thửu lẠI K GÂY LÃNG PHÍ TÀI NGUYÊN
                }catch (InterruptedException ie){
                    Thread.currentThread().interrupt(); // cơ chế nagwts mềm
                }
            }
        }
    }
    @Override
    public void destroy(){
        System.out.println("start graceful shutdown");
        isRunning = false ;
        try {
            executerService.shutdown(); // nghắt mềm ,
            if(!executerService.awaitTermination(20 , TimeUnit.SECONDS)){
                System.out.println("Warning: still have  worker chưa complete task");
            }else {
                System.out.println("Quá tringf ngắt mềm diễn ra hoàn haỏ , các luồng đã dừng an toàn");
                // bóp chết thread con ngay lập tức => an toàn về bộ nhớn , đặc biệt cpu đc giải phóng
                executerService.shutdownNow() ;
            }
        }catch (InterruptedException ex){
            // nếu bị bắn interupt từ os thì nhảy vào đây (k kiên nhẫn đợi 20s)
            executerService.shutdownNow() ; // ép ngắt cứng
            Thread.currentThread().interrupt(); // gắn flag true=> quá trình ngắn cứng diễn ra ở các bean / department others
        }
    }
}

    // cần 1 hamf hủy ở đay đẻ đảm bảo độ an toàn  (Graceful Shutdown)


// khi hệ thống tắt đột ngột mà không theo cơ ché graceful shutdown thì executor.shutNow và interupt sẽ giúp spring,
// ngắt các nguồn , file , conection , tomcart , dảtabse  dừng lại nhanh chóng đạp tất cả các hoạt dộng còn dang giửo , k thực hiện nx
//-> tắt nhanh


// còn ngắt mèm mọi thứu diễn ra 1 cáh từ từ , chậm rãi và sạch sẽ , k cần eps buộc

