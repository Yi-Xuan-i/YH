package com.yixuan.yh.user.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserPreferences {
    Long id;
    Long userId;
    byte[] videoPrefVector;
}
