package com.yixuan.yh.user.pojo.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoInListResponse {
    private Long id;
    private String name;
    private String avatarUrl;
}
