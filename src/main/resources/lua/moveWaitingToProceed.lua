local waitingKey = KEYS[1]
local proceedKey = KEYS[2]
local capacity = tonumber(ARGV[1])

local currentSize = redis.call('ZCARD', proceedKey)
local remain = capacity - currentSize

if remain <= 0 then
    return 0
end

local entries = redis.call('ZRANGE', waitingKey, 0, remain - 1, 'WITHSCORES')
if #entries == 0 then
    return 0
end

for i = 1, #entries, 2 do
    redis.call('ZADD', proceedKey, entries[i+1], entries[i])
    redis.call('ZREM', waitingKey, entries[i])
end

return 1