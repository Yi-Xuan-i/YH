package com.yixuan.yh.product.mapper;

import com.yixuan.yh.product.model.entity.ProductSku;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ProductSkuMapper {

    @Select("select sku_id from product_sku where product_id = #{productId}")
    List<Long> selectSkuIdByProductId(Long productId);

    @Select("select sku_id, price, stock from product_sku where product_id = #{productId}")
    List<ProductSku> selectByProductId(Long productId);

    void insertBatch(Long productId, List<Long> skuIdList);

    @Delete("delete from product_sku where product_id = #{productId}")
    void deleteByProductId(Long productId);

    void deleteBatch(List<Long> skuIdList);
}
