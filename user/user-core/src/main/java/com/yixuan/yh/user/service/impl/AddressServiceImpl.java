package com.yixuan.yh.user.service.impl;

import com.yixuan.yh.user.mapper.AddressMapper;
import com.yixuan.yh.user.mapstruct.AddressMapstruct;
import com.yixuan.yh.user.pojo.entity.UserAddress;
import com.yixuan.yh.user.pojo.request.AddressResponse;
import com.yixuan.yh.user.pojo.request.PutAddressRequest;
import com.yixuan.yh.common.utils.SnowflakeUtils;
import com.yixuan.yh.user.service.AddressService;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    private AddressMapper addressMapper;
    @Autowired
    private SnowflakeUtils snowflakeUtils;

    @Override
    @Transactional
    public Long postAddress(Long userId, PutAddressRequest putAddressRequest) {
        UserAddress userAddress = AddressMapstruct.INSTANCE.putAddressRequestToUserAddress(putAddressRequest);
        userAddress.setAddressId(snowflakeUtils.nextId());
        userAddress.setUserId(userId);

        // 如果选择默认，则去掉“已有的默认”
        if (userAddress.getIsDefault()) {
            addressMapper.updateDefaultByUserId(userId);
        }

        addressMapper.insert(userAddress);
        return userAddress.getAddressId();
    }

    @Override
    public void deleteAddress(Long addressId) {
        addressMapper.delete(addressId);
    }

    @Override
    public void putAddress(Long userId, Long addressId, PutAddressRequest putAddressRequest) throws BadRequestException {
        // 鉴权
        if (!userId.equals(addressMapper.selectUserIdByAddressId(addressId))) {
            throw new BadRequestException("你没有权限！");
        }

        UserAddress userAddress = AddressMapstruct.INSTANCE.putAddressRequestToUserAddress(putAddressRequest);

        // 如果选择默认，则去掉“已有的默认”
        if (userAddress.getIsDefault()) {
            addressMapper.updateDefaultByUserId(userId);
        }

        addressMapper.update(addressId, userAddress);
    }

    @Override
    public List<AddressResponse> getAddresses(Long userId) {
        List<UserAddress> userAddressList = addressMapper.selectByUserId(userId);
        List<AddressResponse> addressResponseList = new ArrayList<>(userAddressList.size());
        for (UserAddress userAddress : userAddressList) {
            addressResponseList.add(AddressMapstruct.INSTANCE.userAddressToAddressResponse(userAddress));
        }
        return addressResponseList;
    }

    @Override
    public AddressResponse getDefaultAddress(Long userId) {
        return AddressMapstruct.INSTANCE.userAddressToAddressResponse(addressMapper.selectDefaultAddress(userId));
    }
}
