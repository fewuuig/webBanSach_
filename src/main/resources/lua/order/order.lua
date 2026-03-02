local stock = redis.call("GET" , KEYS[1]) -- số lươngj sách conf lại trong kho

if not stock then
return -1 -- kho không tồn tại
end
if stock and tonumber(stock) then
	if stock < 0  then
		return -2 -- kho bị âm
	end
end
if not  AGRV[2] then
	return -4 -- sối lượng múa phải là chữ sóo (number)
end
if ARGV[2] and tonumber(ARGV[2]) then
	if ARGV[2] <= 0 then
		return -3 -- số lượng muốn mua khôing hợp lệ
	end
end

if stock < ARGV[2]  then
	return 0 -- kho không đủ
end
redis.call("XADD" , KEYS[2] , "*" ,
          "request_id" ,ARGV[1] ,
          "tenDangNhap" ,ARGV[8]
          "maSach" ,ARGV[3] ,
          "maGiam",ARGV[4] ,
          "soLuong",ARGV[2] ,
          "maDiaChiGiaoHang" ,ARGV[5]  ,
          "maHinhThucThanhToan", ARGV[6],
          "maHinhThucGiaoHang" ,ARGV[7]
          )
redis.call("DECRBY" , KEYS[1]  , ARGV[1] )
-- ARGV[1] request_id
-- ARGV[2] số lượng muôns mua
-- ARGV[3] maSach
-- ARGV[4] maGiam
-- ARGV[2] soLuong
-- ARGV[5] maDiaChiGiaoHang
-- ARGV[6] maHinhThucThanhToan
-- ARGV[7] maHinhThucGiaoHang
-- ARGV[8] tenDangNhap