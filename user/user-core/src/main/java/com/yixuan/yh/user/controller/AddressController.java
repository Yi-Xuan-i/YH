package com.yixuan.yh.user.controller;

import com.yixuan.yh.user.pojo.response.AddressResponse;
import com.yixuan.yh.user.pojo.request.PutAddressRequest;
import com.yixuan.yh.common.response.Result;
import com.yixuan.yh.common.utils.UserContext;
import com.yixuan.yh.user.service.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Address")
@RestController
@RequestMapping("/me/address")
public class AddressController {

    @Autowired
    private AddressService addressService;

    @Operation(summary = "新增收货地址")
    @PostMapping
    public Result<Long> postAddress(@RequestBody PutAddressRequest putAddressRequest) {
        return Result.success(addressService.postAddress(UserContext.getUser(), putAddressRequest));
    }

    @Operation(summary = "删除收货地址")
    @DeleteMapping("/{addressId}")
    public Result<Void> deleteAddress(@PathVariable Long addressId) {
        addressService.deleteAddress(addressId);
        return Result.success();
    }

    @Operation(summary = "修改收货地址")
    @PutMapping("/{addressId}")
    public Result<Void> putAddress(@PathVariable Long addressId, @RequestBody PutAddressRequest putAddressRequest) throws BadRequestException {
        addressService.putAddress(UserContext.getUser(), addressId, putAddressRequest);
        return Result.success();
    }

    @Operation(summary = "获取收货地址列表")
    @GetMapping
    public Result<List<AddressResponse>> getAddresses() {
        return Result.success(addressService.getAddresses(UserContext.getUser()));
    }

    @Operation(summary = "获取默认地址")
    @GetMapping("/default")
    public Result<AddressResponse> getDefaultAddress() {
        return Result.success(addressService.getDefaultAddress(UserContext.getUser()));
    }
}
