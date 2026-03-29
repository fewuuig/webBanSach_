local removed = 0

if #ARGV > 0 then
    removed = removed + redis.call("ZREM", KEYS[1], unpack(ARGV))
    removed = removed + redis.call("ZREM", KEYS[2], unpack(ARGV))
end

for i = 1, #ARGV do
    local key = KEYS[3] .. ARGV[i]
    removed = removed + redis.call("DEL", key)
end

return removed