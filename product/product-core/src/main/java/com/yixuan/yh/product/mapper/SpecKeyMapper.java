package com.yixuan.yh.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yixuan.yh.product.pojo.model.entity.SpecKey;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SpecKeyMapper extends BaseMapper<SpecKey> {
    @Insert("insert into spec_key (key_id, key_name) values(#{id}, #{key})")
    void insert(Long id, String key);

    void deleteBatch(List<Long> keyIdList);
}
