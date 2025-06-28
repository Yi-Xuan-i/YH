package com.yixuan.yh.ai.repository;

import com.yixuan.yh.ai.entity.Intent;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface IntentRepository extends R2dbcRepository<Intent, Long> {
    @Query("select response from intent where intent = :intent order by rand() limit 1")
    Mono<String> findByIntentRandomly(String intent);
}
