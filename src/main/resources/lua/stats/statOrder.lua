------------------------------------
-- ARGV
-- 1 : số đơn hàng
-- 2 : số sách mua
-- 3 : doanh số
---------------------------------------

redis.call("HINCRBY" ,KEYS[1] ,"orders",ARGV[1])
redis.call("HINCRBY" ,KEYS[1] ,"books" ,ARGV[2])
redis.call("HINCRBY" ,KEYS[1] ,"revenue",ARGV[3])