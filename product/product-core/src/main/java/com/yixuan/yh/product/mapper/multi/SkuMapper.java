package com.yixuan.yh.product.mapper.multi;

import com.yixuan.yh.product.pojo.model.multi.SkuSpecInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SkuMapper {

     List<Long> selectMerchantIdBySkuIds(List<Long> skuIdList);

     List<SkuSpecInfo> selectSpecsBySkuIds(List<Long> skuIdList);
}
