------------------------------------
--ARGV
--1:priceFrom
--2:priceTo
--3:offset
--4:size
-------------------------------------
-------------------------------------
--KEYS
--price
-------------------------------------
if not ARGV[3] and not ARGV[4] then
	return redis.call("ZRANGEBYSCORE" , KEYS[1], ARGV[1] , ARGV[2] )
end
return redis.call("ZRANGEBYSCORE" , KEYS[1], ARGV[1] , ARGV[2] ,"LIMIT",ARGV[3] ,ARGV[4])