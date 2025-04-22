local keys = KEYS
local value = ARGV[1]

for i=1, #keys do
	if redis.call('get', keys[i]) ~= value then
		return 0;
	end
end

return 1;