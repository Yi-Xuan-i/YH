package com.yixuan.yh.user.mapper;

import com.yixuan.yh.user.pojo.entity.UserAddress;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AddressMapper {
    @Insert("insert into user_address (address_id, user_id, is_default, receiver_name, receiver_phone, province, city, district, detail_address) values(#{addressId}, #{userId}, #{isDefault}, #{receiverName}, #{receiverPhone}, #{province}, #{city}, #{district}, #{detailAddress})")
    void insert(UserAddress userAddress);

    @Update("update user_address set is_default = false where user_id = #{userId} and is_default = true")
    void updateDefaultByUserId(Long userId);

    @Select("select address_id, is_default, receiver_name, receiver_phone, province, city, district, detail_address from user_address where user_id = #{userId} order by is_default desc")
    List<UserAddress> selectByUserId(Long userId);

    @Select("select user_id from user_address where address_id = #{addressId}")
    Long selectUserIdByAddressId(Long addressId);

    @Update("update user_address set is_default = #{userAddress.isDefault}, receiver_name = #{userAddress.receiverName}, receiver_phone = #{userAddress.receiverPhone}, province = #{userAddress.province}, city = #{userAddress.city}, district = #{userAddress.district}, detail_address = #{userAddress.detailAddress} where address_id = #{addressId}")
    void update(Long addressId, UserAddress userAddress);

    @Delete("delete from user_address where address_id = #{addressId}")
    void delete(Long addressId);

    @Select("select address_id, is_default, receiver_name, receiver_phone, province, city, district, detail_address from user_address where user_id = #{userId} order by is_default desc limit 1")
    UserAddress selectDefaultAddress(Long userId);
}
