package com.yixuan.yh.user.mapper;

import com.yixuan.yh.user.pojo.entity.Merchant;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface MerchantMapper {
    @Select("select count(*) from merchant where merchant_id = #{userId}")
    Boolean selectIsMerchant(Long userId);

    @Select("select name, contact_phone, avatar_url, certification_status from merchant where merchant_id = #{userId}")
    Merchant selectBasic(Long userId);

    @Insert("insert into merchant (merchant_id, name, contact_phone, avatar_url) values(#{merchantId}, #{name}, #{contactPhone}, #{avatarUrl}) ")
    void insert(Merchant merchant);
}
