----------------------------------------------
--ARGV
--1 Array id
----------------------------------------------
--KEYS
--1 : page_book_id
--2 : page_book_id_category:{idCategory}
--3 : book_info:{idBook}
----------------------------------------------------------------

redis.call("ZREM" , KEYS[1] ,unpack(ARGV) )
redis.call("ZREM" , KEYS[2] ,unpack(ARGV) )

for i =1,#ARGV  do
     redis.call("DEL" , KEYS[3]..ARGV[i])
end
