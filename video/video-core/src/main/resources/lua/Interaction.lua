-- 根据key和hashkey获取值并条件性反转
-- KEYS[1]: 主键名
-- ARGV[1]: 哈希字段名
-- ARGV[2]: 要比较的字符串值

-- 获取当前值
local current_value = redis.call('HGET', KEYS[1], ARGV[1])

-- 如果值不存在，返回0
if current_value == false then
    return 0
end

-- 如果值与传入参数相同，返回1（操作失败）
if current_value == ARGV[2] then
    return 1
end

-- 值不同，执行反转操作（"0"变"1"，"1"变"0"）
local new_value
if current_value == "0" then
    new_value = "1"
elseif current_value == "1" then
    new_value = "0"
else
    -- 如果当前值不是"0"或"1"，保持不变（或根据需求处理）
    new_value = current_value
end

redis.call('HSET', KEYS[1], ARGV[1], new_value)

-- 如果提供了过期时间参数，则设置键的过期时间
if ARGV[3] and tonumber(ARGV[3]) > 0 then
    redis.call('EXPIRE', KEYS[1], tonumber(ARGV[3]))
end

-- 返回2表示操作成功
return 2