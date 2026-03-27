----------------------------------------------
--ARGV
--1 : bookid
--2 : soLuong
----------------------------------------------
--KEYS
--1 : page_book_id
--2 : page_book_id_category:{idCategory}
--3 : book:{bookId}
----------------------------------------------------------------
-- thêm vào key1
redis.call("ZADD" , KEYS[1] , ARGV[1] ,ARGV[1])

-- thêm vaof key2
redis.call("ZADD" ,KEYS[2] , ARGV[1],ARGV[1])

-- thêm vào key3
redis.call("SET" , KEYS[3] , ARGV[2])