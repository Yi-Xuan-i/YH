package com.yixuan.yh.user.feign;

import com.yixuan.yh.common.response.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "userService", contextId = "UserPrivateClient")
public interface UserPrivateClient {
    @GetMapping("/user/api/private/name")
    Result<String> getName(@RequestParam Long id);
}
