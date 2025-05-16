package com.yixuan.yh.admin.service.impl;

import com.yixuan.yh.admin.entity.Admin;
import com.yixuan.yh.admin.mapper.AdminMapper;
import com.yixuan.yh.admin.mapper.AdminPermissionMapper;
import com.yixuan.yh.admin.mapstruct.AccountMapStruct;
import com.yixuan.yh.admin.request.PostAccountRequest;
import com.yixuan.yh.admin.request.PutAccountRequest;
import com.yixuan.yh.admin.response.AccountResponse;
import com.yixuan.yh.admin.service.AccountService;
import com.yixuan.yh.commom.utils.SnowflakeUtils;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AccountServiceImpl implements AccountService {

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private AdminPermissionMapper adminPermissionMapper;

    @Autowired
    private SnowflakeUtils snowflakeUtils;

    @Override
    public void postAccount(PostAccountRequest postAccountRequest) throws BadRequestException {
        /* 用户名唯一性 */
        if (adminMapper.selectIsNameExist(postAccountRequest.getName())) {
            throw new BadRequestException("用户名已经被注册！");
        }

        Admin admin = AccountMapStruct.INSTANCE.addAccountRequestToAdmin(postAccountRequest);
        admin.setId(snowflakeUtils.nextId());
        admin.setEncodedPassword(passwordEncoder.encode(postAccountRequest.getPassword()));
        adminMapper.insert(admin);
        // 增加权限
        adminPermissionMapper.insertAdminPermissions(admin.getId(), postAccountRequest.getPermissionList());
    }

    @Override
    public void deleteAccount(Long id) {
        adminMapper.delete(id);
    }

    @Override
    public void updateAccount(PutAccountRequest putAccountRequest) {
        adminMapper.update(putAccountRequest);
    }

    @Override
    public List<AccountResponse> getAccounts(Long id, String name, String createdTime) {
        List<Admin> adminList = adminMapper.selectByCond(id, name, createdTime);
        List<AccountResponse> accountResponseList = new ArrayList<>();
        for (Admin admin : adminList) {
            accountResponseList.add(AccountMapStruct.INSTANCE.adminToAccountResponse(admin));
        }

        return accountResponseList;
    }

    @Override
    public List<AccountResponse> getAllAccounts() {
        List<Admin> adminList = adminMapper.selectAll();
        List<AccountResponse> accountResponseList = new ArrayList<>();
        for (Admin admin : adminList) {
            accountResponseList.add(AccountMapStruct.INSTANCE.adminToAccountResponse(admin));
        }

        return accountResponseList;
    }

    @Override
    public void putPassword(Long id, String password) {
        String encodedPassword = passwordEncoder.encode(password);
        adminMapper.updatePassword(id, encodedPassword);
    }
}
