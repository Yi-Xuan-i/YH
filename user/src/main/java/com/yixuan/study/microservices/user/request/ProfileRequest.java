package com.yixuan.study.microservices.user.request;

import lombok.Data;

@Data
public class ProfileRequest {
    String name;
    String bio;
}
