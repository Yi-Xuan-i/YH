package com.yixuan.yh.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yixuan.yh.user.pojo.entity.Merchant;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface MerchantMapper extends BaseMapper<Merchant> {
    @Select("select count(*) from merchant where merchant_id = #{userId}")
    Boolean selectIsMerchant(Long userId);

    @Select("select name, contact_phone, avatar_url, certification_status from merchant where merchant_id = #{userId}")
    Merchant selectBasic(Long userId);
}
