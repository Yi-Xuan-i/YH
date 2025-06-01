package com.yixuan.yh.product.mapper.multi;

import com.yixuan.yh.product.model.multi.SkuSpecInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SkuMapper {

     List<SkuSpecInfo> selectSpecsBySkuIds(List<Long> skuIdList);
}
