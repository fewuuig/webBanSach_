-------------------------------------------------------
-------ARGV
--1 sender
--2 content
--3 sendToUser

-------------------------------------------------------

-------------------------------------------------------
-------KEYS
--1 chat-stream
-------------------------------------------------------


redis.call("XADD" , KEYS[1] ,"*",
            "sender" , ARGV[1],
            "content" , ARGV[2],
            "sendToUser" , ARGV[3],
            "timestamp",ARGV[4] ,
            "messageId",ARGV[5]
)
return 1 -- thành công thêm vào stream