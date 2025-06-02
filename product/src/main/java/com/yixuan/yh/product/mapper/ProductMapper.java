package com.yixuan.yh.product.mapper;

import com.yixuan.yh.product.model.entity.Product;
import com.yixuan.yh.product.response.ProductManageItemResponse;
import com.yixuan.yh.product.response.ProductDetailResponse;
import com.yixuan.yh.product.response.ProductSummaryResponse;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ProductMapper {
    @Select("SELECT product_id, title, cover_url, price, sales_volume \n" +
            "FROM product \n" +
            "WHERE product_id >= (\n" +
            "    SELECT MIN(product_id) + FLOOR(RAND() * (MAX(product_id) - MIN(product_id))) \n" +
            "    FROM product\n" +
            ") \n" +
            "LIMIT 5;")
    List<ProductSummaryResponse> selectList();

    @Select("select product_id, title, description, price, stock, status, sales_volume, rating, created_at, updated_at from product")
    List<ProductManageItemResponse> selectMerchantProducts(Long user);

    @Select("select title, cover_url, description, price from product where product_id = #{productId}")
    Product selectEditBasicData(Long productId);

    @Delete("delete from product where product_id = #{productId}")
    void deleteByProductId(Long productId);

    @Select("select product_id, title, description from product where product_id = #{productId}")
    Product selectPartOfDetail(Long productId);

    void updateBasicInfo(Product product);

    @Insert("insert into product (product_id, merchant_id, title, cover_url, description, price) values(#{productId}, #{merchantId}, #{title}, #{coverUrl}, #{description}, #{price})")
    void insertBasicInfo(Product product);

    @Select("select merchant_id from product where product_id = #{productId}")
    Long selectMerchantIdByProductId(Long productId);
}
