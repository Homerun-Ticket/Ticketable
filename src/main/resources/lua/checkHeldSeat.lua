local keys = KEYS
local value = ARGV[1]

for i=1, #keys do
	if redis.call('get', keys[i])!= value
		return 0;
end

return 1;