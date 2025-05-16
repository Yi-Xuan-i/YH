package com.yixuan.yh.admin.mapstruct;

import com.yixuan.yh.admin.entity.Admin;
import com.yixuan.yh.admin.request.PostAccountRequest;
import com.yixuan.yh.admin.response.AccountResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AccountMapStruct {
    AccountMapStruct INSTANCE = Mappers.getMapper(AccountMapStruct.class);

    AccountResponse adminToAccountResponse(Admin admin);

    Admin addAccountRequestToAdmin(PostAccountRequest postAccountRequest);
}
