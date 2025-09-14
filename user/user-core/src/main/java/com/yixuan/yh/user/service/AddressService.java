package com.yixuan.yh.user.service;

import com.yixuan.yh.user.pojo.response.AddressResponse;
import com.yixuan.yh.user.pojo.request.PutAddressRequest;
import org.apache.coyote.BadRequestException;

import java.util.List;

public interface AddressService {
    Long postAddress(Long userId, PutAddressRequest putAddressRequest);

    void deleteAddress(Long addressId);

    void putAddress(Long userId, Long addressId, PutAddressRequest putAddressRequest) throws BadRequestException;

    List<AddressResponse> getAddresses(Long userId);

    AddressResponse getDefaultAddress(Long userId);
}
