local waitingKey = KEYS[1]
local proceedKey = KEYS[2]
local capacity = tonumber(ARGV[1])

local entries = redis.call('ZPOPMIN', waitingKey, capacity)
if #entries == 0 then
    return 0
end

for i = 1, #entries, 2 do
    redis.call('SET', "proceed:"..entries[i], "1", "NX", "EX", 300)
end

return 1