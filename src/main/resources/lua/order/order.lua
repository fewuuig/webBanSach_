-- KEYS
-- KEYS[1] = order-stream
-- KEYS[2] = rate limit key

-- ARGV
-- 1 request_id
-- 2 items json
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

local items = cjson.decode(ARGV[2])


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

    local stockKey = "book:" .. maSach
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

end


------------------------------------------------
-- 4 giảm kho
------------------------------------------------

for _, item in ipairs(items) do

    local maSach = tonumber(item["maSach"])
    local soLuong = tonumber(item["soLuong"])

    local stockKey = "book:" .. maSach

    redis.call("DECRBY", stockKey, soLuong)

end


------------------------------------------------
-- 5 push order vào stream
------------------------------------------------

redis.call(
    "XADD",
    KEYS[1],
    "*",
    "request_id", ARGV[1],
    "tenDangNhap", ARGV[7],
    "items", ARGV[2],
    "maGiam", ARGV[3],
    "maDiaChiGiaoHang", ARGV[4],
    "maHinhThucThanhToan", ARGV[5],
    "maHinhThucGiaoHang", ARGV[6]
)

return 1