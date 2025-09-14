package com.yixuan.yh.admin.controller;

import com.yixuan.yh.admin.annotations.RequiresPermission;
import com.yixuan.yh.admin.request.PostAccountRequest;
import com.yixuan.yh.admin.request.PutAccountRequest;
import com.yixuan.yh.admin.response.AccountResponse;
import com.yixuan.yh.admin.service.AccountService;
import com.yixuan.yh.common.response.Result;
import com.yixuan.yh.common.utils.UserContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "Account")
@RestController
@RequestMapping("/account")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @Operation(summary = "新增管理员")
    @PostMapping
    @RequiresPermission({"ACCOUNT:INSERT"})
    public Result<Void> postAccount(@RequestBody PostAccountRequest postAccountRequest) throws BadRequestException {
        accountService.postAccount(postAccountRequest);
        return Result.success();
    }

    @Operation(summary = "删除管理员")
    @DeleteMapping("/{id}")
    @RequiresPermission({"ACCOUNT:DELETE"})
    public Result<Void> deleteAccount(@PathVariable Long id) {
        accountService.deleteAccount(id);
        return Result.success();
    }

    @Operation(summary = "修改管理员信息")
    @PutMapping
    @RequiresPermission({"ACCOUNT:UPDATE"})
    public Result<Void> updateAccount(@RequestBody PutAccountRequest putAccountRequest) {
        accountService.updateAccount(putAccountRequest);
        return Result.success();
    }

    @Operation(summary = "搜索管理员")
    @GetMapping
    @RequiresPermission({"ACCOUNT:SELECT"})
    public Result<List<AccountResponse>> getAccounts(@RequestParam(required = false) Long id,
                                                     @RequestParam(required = false) String name,
                                                     @RequestParam(required = false) String createdTime) {
        return Result.success(accountService.getAccounts(id, name, createdTime));
    }

    @Operation(summary = "获取管理员")
    @GetMapping("/all")
    @RequiresPermission({"ACCOUNT:SELECT"})
    public Result<List<AccountResponse>> getAllAccounts() {
        return Result.success(accountService.getAllAccounts());
    }

    @Operation(summary = "修改管理员密码")
    @PutMapping("/password")
    public Result<Void> putPassword(@RequestBody Map<String, String> map) {
        accountService.putPassword(UserContext.getUser(), map.get("password"));
        return Result.success();
    }
}
