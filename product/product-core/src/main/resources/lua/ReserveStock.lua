-- KEYS[1] 商品库存的键名
local key = KEYS[1]
local quantity = ARGV[1]

-- 检查键是否存在
if redis.call("EXISTS", key) == 0 then
    return 2
end

-- 获取当前库存（转换为数字）
local stock = tonumber(redis.call("GET", key))

-- 判断库存是否充足
if stock > 0 then
    -- 扣减库存（原子操作）
    redis.call("DECRBY", key, quantity)
    return 1
else
    return 0
end