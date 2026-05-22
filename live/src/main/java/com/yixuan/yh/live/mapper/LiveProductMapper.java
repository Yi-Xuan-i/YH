package com.yixuan.yh.live.mapper;

import com.yixuan.yh.live.entity.LiveProduct;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface LiveProductMapper {
    @Insert("insert into live_product (id, room_id, product_id) values(#{id}, #{roomId}, #{productId})")
    void insert(LiveProduct liveProduct);

    @Select("select product_id from live_product where room_id = #{roomId}")
    List<Long> selectProductIdsByRoomId(Long roomId);

    @Select("select product_id from live_product where product_id = #{productId} limit 1")
    Long selectProductId(Long productId);
}
