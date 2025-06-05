package com.yixuan.study.microservices.user.mapstruct;

import com.yixuan.study.microservices.user.entity.UserAddress;
import com.yixuan.study.microservices.user.request.AddressResponse;
import com.yixuan.study.microservices.user.request.PutAddressRequest;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AddressMapstruct {
    AddressMapstruct INSTANCE = Mappers.getMapper(AddressMapstruct.class);

    UserAddress putAddressRequestToUserAddress(PutAddressRequest putAddressRequest);
    AddressResponse userAddressToAddressResponse(UserAddress userAddress);
}
