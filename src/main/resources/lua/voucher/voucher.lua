local stockStr = redis.call("GET", KEYS[1])
if not stockStr then
    return -1
end

local stock = tonumber(stockStr)
if not stock then
    return -4 -- dữ liệu không phải số
end

if stock <= 0 then
    return 0
end

local userUsedStr = redis.call("GET", KEYS[2])
local userUsed = 0

if userUsedStr then
    userUsed = tonumber(userUsedStr)
    if not userUsed then
        return -5 -- dữ liệu userUsed lỗi
    end
end

local userMax = tonumber(ARGV[1])
if not userMax then
    return ARGV[1]  -- ARGV lỗi
end

if userUsed < 0 then
    return -3
end

if userUsed >= userMax then
    return -2
end

redis.call("INCR", KEYS[2])
redis.call("DECR", KEYS[1])
--redis.call("SET", KEYS[3] , 1 , "EX" ,300 ) -- tính theo giây
-- push vào stream
redis.call("XADD" , KEYS[3] , "*" , "voucherID" , ARGV[2] , "username",ARGV[3] , "request_id" , ARGV[4])

return 1