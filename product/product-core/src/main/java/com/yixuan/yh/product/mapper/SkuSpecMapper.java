package com.yixuan.yh.product.mapper;

import com.yixuan.yh.product.pojo.model.entity.SkuSpec;
import com.yixuan.yh.product.pojo.request.PostSkuSpecRequest;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SkuSpecMapper {

    void insertBatch1(List<Long> skuIdList, Long keyId, Long valueId);
    void insertBatch2(List<Long> skuIdList, List<List<PostSkuSpecRequest.SpecId>> skuList);

    List<SkuSpec> selectKeyValueBatch(List<Long> skuIdList);

    List<Long> selectSkuIdsByProductAndSpec(Long productId, Long keyId, Long valueId);

    Integer countValuesByProductAndKey(Long productId, Long keyId);

    void deleteBatch(List<Long> skuIdList);

    void deleteByProductAndSpec(Long productId, Long keyId, Long valueId);
}
