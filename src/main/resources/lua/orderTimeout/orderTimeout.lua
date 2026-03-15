local now = ARGV[1] ;
if not tonumber(now) then
	return -1 -- thười gian hiện tại không hợp lệ
end

local orderIds = redis.call("ZRANGEBYSCORE" , KEYS[1] , 0 , now , 'LIMIT' , 0 , 100)
for i,orderId  in ipairs(orderIds) do
	redis.call("ZREM" ,KEYS[1] , orderId)
end
return orderIds ;
