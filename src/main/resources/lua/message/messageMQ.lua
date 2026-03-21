-------------------------------------------------------
-------ARGV
--1 sender
--2 content
--3 timestamp
--4 sendToUser
--5 roomId
-------------------------------------------------------

-------------------------------------------------------
-------KEYS
--1 chat:user:{roomid}
-------------------------------------------------------


redis.call("XADD" , KEYS[1] ,"*",
            "sender" , ARGV[1],
            "content" , ARGV[2],
            "timestamp" , ARGV[3],
            "sendToUser" , ARGV[4],
            "roomId",ARGV[5]
)
return 1 -- thành công thêm vào stream