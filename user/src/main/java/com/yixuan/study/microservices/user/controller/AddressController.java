package com.yixuan.study.microservices.user.controller;

import com.yixuan.study.microservices.user.request.AddressResponse;
import com.yixuan.study.microservices.user.request.PutAddressRequest;
import com.yixuan.study.microservices.user.service.AddressService;
import com.yixuan.yh.commom.response.Result;
import com.yixuan.yh.commom.utils.UserContext;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/me/address")
public class AddressController {

    @Autowired
    private AddressService addressService;

    @PostMapping
    public Result<Long> postAddress(@RequestBody PutAddressRequest putAddressRequest) {
        return Result.success(addressService.postAddress(UserContext.getUser(), putAddressRequest));
    }

    @DeleteMapping("/{addressId}")
    public Result<Void> deleteAddress(@PathVariable Long addressId) {
        addressService.deleteAddress(addressId);
        return Result.success();
    }

    @PutMapping("/{addressId}")
    public Result<Void> putAddress(@PathVariable Long addressId, @RequestBody PutAddressRequest putAddressRequest) throws BadRequestException {
        addressService.putAddress(UserContext.getUser(), addressId, putAddressRequest);
        return Result.success();
    }

    @GetMapping
    public Result<List<AddressResponse>> getAddresses() {
        return Result.success(addressService.getAddresses(UserContext.getUser()));
    }

    @GetMapping("/default")
    public Result<AddressResponse> getDefaultAddress() {
        return Result.success(addressService.getDefaultAddress(UserContext.getUser()));
    }
}
