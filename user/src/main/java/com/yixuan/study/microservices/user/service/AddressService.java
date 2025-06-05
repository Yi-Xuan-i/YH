package com.yixuan.study.microservices.user.service;

import com.yixuan.study.microservices.user.request.AddressResponse;
import com.yixuan.study.microservices.user.request.PutAddressRequest;
import org.apache.coyote.BadRequestException;

import java.util.List;

public interface AddressService {
    Long postAddress(Long userId, PutAddressRequest putAddressRequest);

    void deleteAddress(Long addressId);

    void putAddress(Long userId, Long addressId, PutAddressRequest putAddressRequest) throws BadRequestException;

    List<AddressResponse> getAddresses(Long userId);

    AddressResponse getDefaultAddress(Long userId);
}
