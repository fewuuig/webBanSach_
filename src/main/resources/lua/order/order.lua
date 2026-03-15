-- kiểm limit rate requets
local limit = redis.call("INCR" , KEYS[3])

if limit == 1 then
	redis.call("EXPIRE" , KEYS[3] , 5) -- 5s giới hạn cho phép 1 request đối vưới 1 người
end

if limit >1 then
	return -5 -- spam request quá số lần quy đinh
end


-- check kho
local stock = redis.call("GET" , KEYS[1]) -- số lươngj sách conf lại trong kho

if not stock then
return -1 -- kho không tồn tại
end
if stock and tonumber(stock) then
	if tonumber(stock) < 0  then
		return -2 -- kho bị âm
	end
end
local quantityBuy = tonumber(ARGV[2]) ;
if not  quantityBuy then
	return -4 -- sối lượng múa phải là chữ sóo (number)
end
if quantityBuy then
	if quantityBuy <= 0 then
		return -3 -- số lượng muốn mua khôing hợp lệ
	end
end

if tonumber(stock) < quantityBuy  then
	return 0 -- kho không đủ
end

-- message
redis.call("XADD" , KEYS[2] , "*" ,
          "request_id" ,ARGV[1] ,
          "tenDangNhap" ,ARGV[8],
          "maSach" ,ARGV[3] ,
          "maGiam",ARGV[4] ,
          "soLuong",ARGV[2] ,
          "maDiaChiGiaoHang" ,ARGV[5]  ,
          "maHinhThucThanhToan", ARGV[6],
          "maHinhThucGiaoHang" ,ARGV[7]
          )
redis.call("DECRBY" , KEYS[1]  , quantityBuy )
return 1 ;
-- ARGV[1] request_id
-- ARGV[2] số lượng muôns mua
-- ARGV[3] maSach
-- ARGV[4] maGiam
-- ARGV[2] soLuong
-- ARGV[5] maDiaChiGiaoHang
-- ARGV[6] maHinhThucThanhToan
-- ARGV[7] maHinhThucGiaoHang
-- ARGV[8] tenDangNhap