package com.yixuan.yh.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yixuan.yh.user.pojo.entity.Merchant;
import com.yixuan.yh.user.pojo.request.PutMerchantRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface MerchantMapper extends BaseMapper<Merchant> {
    @Select("select count(*) from merchant where merchant_id = #{userId}")
    Boolean selectIsMerchant(Long userId);

    @Select("select name, contact_phone, avatar_url, certification_status from merchant where merchant_id = #{userId}")
    Merchant selectBasic(Long userId);

    @Update("update merchant set name = #{putMerchantRequest.name}, contact_phone = #{putMerchantRequest.contactPhone} where merchant_id = #{userId}")
    void updateBasic(@Param("userId") Long userId, @Param("putMerchantRequest") PutMerchantRequest putMerchantRequest);

    @Update("update merchant set avatar_url = #{newAvatarUrl} where merchant_id = #{userId}")
    void updateAvatarUrl(@Param("userId") Long userId, @Param("newAvatarUrl") String newAvatarUrl);
}
