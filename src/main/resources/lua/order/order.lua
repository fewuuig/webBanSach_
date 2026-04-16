-- KEYS
-- KEYS[1] = order-stream
-- KEYS[2] = rate limit key

-- ARGV

-- 1 items json
-- 3 maGiam
-- 4 maDiaChi
-- 5 maThanhToan
-- 6 maVanChuyen
-- 7 tenDangNhap

------------------------------------------------
-- 1 rate limit
------------------------------------------------



------------------------------------------------
-- 2 parse JSON
------------------------------------------------

local items = cjson.decode(ARGV[1])


if type(items) == "string" then
    items = cjson.decode(items)
end

if type(items) ~= "table" then
    return -9
end


------------------------------------------------
-- 3 validate + check stock
------------------------------------------------

for _, item in ipairs(items) do

    local maSach = tonumber(item["maSach"])
    local soLuong = tonumber(item["soLuong"])

    if not maSach or not soLuong then
        return -9
    end

    if soLuong <= 0 then
        return -3
    end

    local stockKey = KEYS[1] .. maSach
    local stock = redis.call("GET", stockKey)

    if not stock then
        return -1
    end

    stock = tonumber(stock)

    if stock <= 0 then
        return -2
    end

    if soLuong > stock then
        return 0
    end
    redis.call("DECRBY", stockKey, soLuong)

end


------------------------------------------------
-- 4 giảm kho
------------------------------------------------




------------------------------------------------
-- 5 push order vào stream
------------------------------------------------

return 1