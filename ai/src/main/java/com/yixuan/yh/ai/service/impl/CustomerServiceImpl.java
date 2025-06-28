package com.yixuan.yh.ai.service.impl;

import com.yixuan.yh.ai.cache.IntentResponseCache;
import com.yixuan.yh.ai.constant.RedisKeyConstant;
import com.yixuan.yh.ai.service.CustomerService;
import com.yixuan.yh.ai.utils.ReactiveUserContext;
import com.yixuan.yh.common.response.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final WebClient webClient = WebClient.create("http://127.0.0.1:10010");
    @Autowired
    private ReactiveStringRedisTemplate stringRedisTemplate;
    @Autowired
    private IntentResponseCache intentResponseCache;

    @Override
    public Mono<String> chat(String msg) {
        return ReactiveUserContext.getUserId()
                .flatMap(userId -> {
                            String key = RedisKeyConstant.CUSTOMER_SERVICE_CHAT_STATUS_KEY_PREFIX + userId;
                            return stringRedisTemplate.opsForValue().get(key)
                                    .switchIfEmpty(Mono.defer(() ->
                                                    stringRedisTemplate.opsForValue().set(key, ChatStatus.NORMAL.getStringValue(), Duration.ofMinutes(10))
                                                            .thenReturn(ChatStatus.NORMAL.getStringValue())
                                            )
                                    )
                                    .flatMap(value -> {
                                        switch (ChatStatus.fromStringValue(value)) {
                                            case RETURN_GOODS -> {
                                                try {
                                                    Long.parseLong(msg);
                                                    return stringRedisTemplate.opsForValue().set(RedisKeyConstant.CUSTOMER_SERVICE_CHAT_RETURN_GOODS_ID_KEY_PREFIX + userId, msg)
                                                            .then(stringRedisTemplate.opsForValue().set(key, ChatStatus.RETURN_GOODS_CONFIRM.getStringValue(), Duration.ofMinutes(10))
                                                                    .then(Mono.just("你确认要退货-" + msg + "吗？（确认退货则输入“确认”）")));
                                                } catch (Exception e) {
                                                    return stringRedisTemplate.opsForValue().set(key, ChatStatus.NORMAL.getStringValue(), Duration.ofMinutes(10))
                                                            .then(normalHandler(key, msg));
                                                }
                                            }
                                            case RETURN_GOODS_CONFIRM -> {
                                                if (msg.equals("确认")) {
                                                    return Mono.just("已经成功帮您申请退货！");
                                                }
                                                return stringRedisTemplate.opsForValue().set(key, ChatStatus.NORMAL.getStringValue(), Duration.ofMinutes(10))
                                                        .then(normalHandler(key, msg));
                                            }
                                            default -> {
                                                return normalHandler(key, msg);
                                            }
                                        }

                                    });
                        }
                );
    }

    private Mono<String> normalHandler(String key, String msg) {
        return webClient.get()
                .uri("/predict?text={text}", msg)
                .retrieve()
                .bodyToMono(Result.class)
                .flatMap(intent -> {
                    if (intent.getData().equals("退货")) {
                        return stringRedisTemplate.opsForValue().set(key, ChatStatus.RETURN_GOODS.getStringValue(), Duration.ofMinutes(10))
                                .then(Mono.just("请输入你要退货的订单号"));
                    }
                    return intentResponseCache.getResponse((String) intent.getData());
                });
    }

    enum ChatStatus {
        NORMAL(0),
        RETURN_GOODS(1),
        RETURN_GOODS_CONFIRM(2);

        private final int value;

        ChatStatus(int i) {
            value = i;
        }

        public static ChatStatus fromValue(int value) {
            for (ChatStatus status : ChatStatus.values()) {
                if (status.value == value) {
                    return status;
                }
            }
            throw new IllegalArgumentException("无效的 ChatStatus value: " + value);
        }

        public static ChatStatus fromStringValue(String value) {
            return fromValue(Integer.parseInt(value));
        }

        public int getValue() {
            return value;
        }

        public String getStringValue() {
            return Integer.toString(value);
        }
    }
}
