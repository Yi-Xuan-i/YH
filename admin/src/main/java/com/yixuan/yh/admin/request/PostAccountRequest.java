package com.yixuan.yh.admin.request;

import lombok.Data;

import java.util.List;

@Data
public class PostAccountRequest {
    private String name;
    private String password;
    private List<String> permissionList;
}