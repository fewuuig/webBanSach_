-- ==========================================
-- ONE-SHOT ORDER SCRIPT (Atomic & No JSON Parsing)
-- ==========================================

-- KEYS:
-- KEYS[1] = Rate Limit Key (e.g. rate_limit:{ws}:user1)
-- KEYS[2] = Stream Key (e.g. order-stream:{ws}:shard-1)
-- KEYS[3...N] = Stock Keys của từng cuốn sách

-- ARGV:
-- ARGV[1] = Max Request Limit (e.g. 5)
-- ARGV[2] = Limit Window Seconds (e.g. 10)
-- ARGV[3] = request_id
-- ARGV[4] = tenDangNhap
-- ARGV[5] = items (Chuỗi JSON nguyên bản để ném vào stream)
-- ARGV[6] = maGiam
-- ARGV[7] = maDiaChiGiaoHang
-- ARGV[8] = maHinhThucThanhToan
-- ARGV[9] = maHinhThucGiaoHang
-- ARGV[10...N] = Số lượng mua tương ứng với KEYS[3...N]

------------------------------------------------
-- 1. XỬ LÝ RATE LIMIT (Chống Spam API)
------------------------------------------------
--local currentReq = redis.call('INCR', KEYS[1])
--if currentReq == 1 then
--    redis.call('EXPIRE', KEYS[1], tonumber(ARGV[2]))
--end
--if currentReq > tonumber(ARGV[1]) then
--    return -5 -- Lỗi Spam API
--end

------------------------------------------------
-- 2. VALIDATE & CHECK STOCK (Chưa trừ vội)
------------------------------------------------
local numBooks = #KEYS - 2
local quantities = {}

for i = 1, numBooks do
    local qty = tonumber(ARGV[9 + i])
    if not qty or qty <= 0 then
        return -3 -- Số lượng không hợp lệ
    end
    quantities[i] = qty

    local stockKey = KEYS[2 + i]
    local currentStock = redis.call('GET', stockKey)

    if not currentStock then
        return -1 -- Sách không tồn tại trong kho
    end

    if tonumber(currentStock) < qty then
        return 0 -- Kho không đủ (Out of stock)
    end
end

------------------------------------------------
-- 3. TRỪ KHO (Sau khi tất cả đều pass)
------------------------------------------------
for i = 1, numBooks do
    redis.call('DECRBY', KEYS[2 + i], quantities[i])
end

------------------------------------------------
-- 4. PUSH VÀO STREAM (XADD)
------------------------------------------------
redis.call('XADD', KEYS[2], '*',
    'request_id', ARGV[3],
    'tenDangNhap', ARGV[4],
    'items', ARGV[5],
    'maGiam', ARGV[6],
    'maDiaChiGiaoHang', ARGV[7],
    'maHinhThucThanhToan', ARGV[8],
    'maHinhThucGiaoHang', ARGV[9]
)

return 1 -- Thành công tuyệt đối