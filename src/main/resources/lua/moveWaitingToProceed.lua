local waitingKey = KEYS[1]
local proceedKey = KEYS[2]
local capacity = tonumber(ARGV[1])

local currentSize = redis.call('ZCARD', proceedKey)
local remain = capacity - currentSize

if remain <= 0 then
    return 0
end

local entries = redis.call('ZRANGE', waitingKey, 0, remain - 1)
if #entries == 0 then
    return 0
end

local now = redis.call('TIME')  -- [seconds, microseconds]
local timestamp = now[1] * 1000 + math.floor(now[2] / 1000)

for i = 1, #entries do
    redis.call('ZADD', proceedKey, timestamp, entries[i])
    redis.call('ZREM', waitingKey, entries[i])
end

return 1