local bookIds = redis.call("ZREVRANGE" ,KEYS[1] , ARGV[1] , ARGV[2] )
return bookIds
-- ARVG[1] : start
-- ARVG[2] : end