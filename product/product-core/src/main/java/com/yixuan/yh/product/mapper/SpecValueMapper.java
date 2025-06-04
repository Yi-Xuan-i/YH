package com.yixuan.yh.product.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SpecValueMapper {
    @Insert("insert into spec_value (value_id, value_name) values (#{id}, #{value})")
    void insert(Long id, String value);

    void deleteBatch(List<Long> valueIdList);
}
