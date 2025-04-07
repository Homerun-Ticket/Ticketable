local keys = KEYS

for i=1, #keys do
	redis.call('del', keys[i])
end

return 1;