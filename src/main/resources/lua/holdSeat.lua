local keys = KEYS
local value = ARGV[1]
local ttl = tonumber(ARGV[2])

-- 1. 중복 체크
for i = 1, #keys do
  if redis.call('exists', keys[i]) == 1 then
    return 0
  end
end

-- 2. 모두 설정
for i = 1, #keys do
  redis.call('set', keys[i], value)
  redis.call('expire', keys[i], ttl)
end

return 1