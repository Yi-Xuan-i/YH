package com.yixuan.yh.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yixuan.yh.product.pojo.model.entity.Product;
import com.yixuan.yh.product.pojo.response.ProductManageItemResponse;
import com.yixuan.yh.product.pojo.response.ProductSummaryResponse;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ProductMapper extends BaseMapper<Product> {
    @Select("select product_id, title, description, status, sales_volume, rating, created_at, updated_at from product where merchant_id = #{userId}")
    List<ProductManageItemResponse> selectMerchantProducts(Long userId);

    @Select("select product_id from product where merchant_id = #{merchantId}")
    List<Long> selectProductIdsByMerchantId(Long merchantId);

    @Select("select title, cover_url, description, default_sku_id from product where product_id = #{productId}")
    Product selectEditBasicData(Long productId);

    @Delete("delete from product where product_id = #{productId}")
    void deleteByProductId(Long productId);

    @Select("select product_id, merchant_id, title, description, default_sku_id, sales_volume from product where product_id = #{productId}")
    Product selectPartOfDetail(Long productId);

    void updateBasicInfo(Product product);

    @Insert("insert into product (product_id, merchant_id, title, cover_url, description, price) values(#{productId}, #{merchantId}, #{title}, #{coverUrl}, #{description}, #{price})")
    void insertBasicInfo(Product product);

    @Select("select merchant_id from product where product_id = #{productId}")
    Long selectMerchantIdByProductId(Long productId);

    @Select("select merchant_id, title from product where product_id = #{productId}")
    Product selectPartOfOrder(Long productId);

    @Update("update product set sales_volume = coalesce(sales_volume, 0) + #{quantity} where product_id = #{productId}")
    int increaseSalesVolume(@Param("productId") Long productId, @Param("quantity") Integer quantity);
}
