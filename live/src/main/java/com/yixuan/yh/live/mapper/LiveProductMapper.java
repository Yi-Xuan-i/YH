package com.yixuan.yh.live.mapper;

import com.yixuan.yh.live.entity.LiveProduct;
import com.yixuan.yh.live.response.LiveProductItemResponse;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface LiveProductMapper {
    @Insert("insert into live_product (id, room_id, product_id, title, image_url, sales_volume) values(#{id}, #{roomId}, #{productId}, #{title}, #{imageUrl}, #{salesVolume})")
    void insert(LiveProduct liveProduct);

    @Select("select product_id as productId, title, image_url as imageUrl, sales_volume as salesVolume from live_product where room_id = #{roomId}")
    List<LiveProductItemResponse> selectByRoomId(Long roomId);

    @Select("select product_id as productId, title, image_url as imageUrl, sales_volume as salesVolume from live_product where product_id = #{productId}")
    LiveProductItemResponse selectByProductId(Long productId);

    @Select("select product_id as productId, title, cover_url as imageUrl, sales_volume as salesVolume from product where merchant_id = #{merchantId} and product_id = #{productId} and status = 4")
    LiveProduct selectMerchantOnSaleProduct(@Param("merchantId") Long merchantId, @Param("productId") Long productId);
}
