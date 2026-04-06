local stockStr = redis.call("GET", KEYS[1])
if not stockStr then
    return -1
end

local stock = tonumber(stockStr)
if not stock then
    return -4
end

if stock <= 0 then
    return 0
end
if(stock < 1) then
	return -2
end

redis.call("DECR", KEYS[1])
--redis.call("SET", KEYS[3] , 1 , "EX" ,300 ) -- tính theo giây
-- push vào stream
redis.call("XADD" , KEYS[3] , "*" , "voucherID" , ARGV[1] , "username",ARGV[2] , "request_id" , ARGV[3])

return 1