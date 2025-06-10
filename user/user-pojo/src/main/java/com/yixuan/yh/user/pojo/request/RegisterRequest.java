package com.yixuan.yh.user.pojo.request;

import com.yixuan.yh.user.pojo.constant.UserConstant;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotEmpty(message = "手机号不能为空！")
    @Size(max = UserConstant.PHONE_NUMBER_MAX_SIZE, message = "手机号长度不能大于" + UserConstant.PHONE_NUMBER_MAX_SIZE + "！")
    String phoneNumber;
    @NotEmpty(message = "密码不能为空！")
    @Size(min = UserConstant.PASSWORD_MIN_SIZE, max = UserConstant.PASSWORD_MAX_SIZE, message = "密码长度必须在" + UserConstant.PASSWORD_MIN_SIZE + "到" + UserConstant.PASSWORD_MAX_SIZE + "之间！")
    String password;
}
