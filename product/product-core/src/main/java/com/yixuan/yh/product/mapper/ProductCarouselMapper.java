package com.yixuan.yh.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yixuan.yh.product.pojo.model.entity.ProductCarousel;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ProductCarouselMapper extends BaseMapper<ProductCarousel> {
    @Select("select id, url from product_carousel where product_id = #{productId}")
    List<ProductCarousel> selectByProductId(Long productId);

    @Select("select url from product_carousel where product_id = #{productId}")
    List<String> selectUrlByProductId(Long productId);

    @Select("select product_id from product_carousel where id = #{carouselId}")
    Long selectProductIdByCarouselId(Long carouselId);
}
