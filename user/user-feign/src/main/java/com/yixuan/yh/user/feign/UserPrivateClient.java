package com.yixuan.yh.user.feign;

import com.yixuan.yh.common.response.Result;
import com.yixuan.yh.user.pojo.response.UserInfoInListResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "userService", contextId = "UserPrivateClient")
public interface UserPrivateClient {
    @GetMapping("/user/api/private/name")
    Result<String> getName(@RequestParam Long id);

    @GetMapping("/user/api/private/info-in-list")
     Result<List<UserInfoInListResponse>> getUserInfoInList(@RequestParam List<Long> idList);
}
