-- scripts/update_tags.lua
local key = KEYS[1]
local now = tonumber(ARGV[1])
local alpha = tonumber(ARGV[2])
local keep = tonumber(ARGV[3])
local threshold = tonumber(ARGV[4])
local expire = tonumber(ARGV[5])
local weight = tonumber(ARGV[6])

-- 从 ARGV[7] 开始是tag_id
for i = 7, #ARGV do
    local tag_id = tonumber(ARGV[i])

    local s_old = redis.call('ZSCORE', key, tag_id)
    local w_total = weight

    if s_old then
        -- w_current = e^(s_old - alpha * now)
        local w_current = math.exp(tonumber(s_old) - (alpha * now))
        w_total = w_current + weight
    end

    local s_new = math.log(w_total) + (alpha * now)
    redis.call('ZADD', key, s_new, tag_id)
end

-- 只有标签量大时才触发“重型”裁剪
local current_count = redis.call('ZCARD', key)
if current_count >= threshold then
    redis.call('ZREMRANGEBYRANK', key, 0, -(keep + 1))
end
redis.call('EXPIRE', key, expire)
return current_count