package com.yixuan.yh.product.mapper;

import com.yixuan.yh.product.pojo.model.entity.ProductCarousel;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ProductCarouselMapper {
    @Insert("insert into product_carousel (id, url, product_id) values(#{id}, #{url}, #{productId})")
    void insert(ProductCarousel productCarousel);

    @Select("select id, url from product_carousel where product_id = #{productId}")
    List<ProductCarousel> selectByProductId(Long productId);

    @Select("select url from product_carousel where product_id = #{productId}")
    List<String> selectUrlByProductId(Long productId);

    @Select("select product_id from product_carousel where id = #{carouselId}")
    Long selectProductIdByCarouselId(Long carouselId);

    @Delete("delete from product_carousel where id = #{carouselId}")
    void delete(Long carouselId);
}
