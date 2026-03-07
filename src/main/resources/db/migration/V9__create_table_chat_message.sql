CREATE table chat_message(
   room_id INT primary key  auto_increment not null ,
    sender varchar(30) ,
    content varchar(1000) ,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);