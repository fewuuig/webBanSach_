------------------------------
--KEYS :
--1 :auto-complete-search:{prefix}
--------------------------------
redis.call("LPUSH" ,KEYS[1] , unpack(ARGV) )
redis.call("EXPIRE" ,KEYS[1] , 300 )