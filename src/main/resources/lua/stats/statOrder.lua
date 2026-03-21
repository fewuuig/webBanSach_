------------------------------------
-- ARGV
-- 1 : số đơn hàng
-- 2 : số sách mua
-- 3 : doanh số
---------------------------------------
local order = tonumber(ARGV[1])
local book = tonumber(ARGV[2])
local revenue = tonumber(ARGV[3])

redis.call("HINCRBY" ,KEYS[1] ,"orders",order)
redis.call("HINCRBY" ,KEYS[1] ,"books" ,book)
redis.call("HINCRBYFLOAT" ,KEYS[1] ,"revenue",revenue)