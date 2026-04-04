
for i=1, #ARGV[1] , 2 do

    local maSach = tonumber(ARGV[i])
    local soLuong = tonumber(ARGV[i+1])

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
    redis.call("DECRBY" , stockKey ,soLuong )

end

