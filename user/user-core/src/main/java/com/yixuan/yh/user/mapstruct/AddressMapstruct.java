package com.yixuan.yh.user.mapstruct;

import com.yixuan.yh.user.pojo.entity.UserAddress;
import com.yixuan.yh.user.pojo.response.AddressResponse;
import com.yixuan.yh.user.pojo.request.PutAddressRequest;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AddressMapstruct {
    AddressMapstruct INSTANCE = Mappers.getMapper(AddressMapstruct.class);

    UserAddress putAddressRequestToUserAddress(PutAddressRequest putAddressRequest);
    AddressResponse userAddressToAddressResponse(UserAddress userAddress);
}
