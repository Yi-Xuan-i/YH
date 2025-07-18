package com.yixuan.yh.user.feign;

import com.yixuan.yh.common.response.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "userService", contextId = "UserFollowPrivateClient")
public interface UserFollowPrivateClient {
    @GetMapping("/user/api/private/follow/status")
    Result<List<Boolean>> getFollowStatus(@RequestParam Long followerId, @RequestParam List<Long> followeeIdList);
}
