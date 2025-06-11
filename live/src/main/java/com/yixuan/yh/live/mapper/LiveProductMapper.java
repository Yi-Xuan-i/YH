package com.yixuan.yh.live.mapper;

import com.yixuan.yh.live.entity.LiveProduct;
import com.yixuan.yh.live.response.GetLiveProductResponse;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface LiveProductMapper {
    @Insert("insert into live_product (id, room_id, name, price, stock, image_url) values(#{id}, #{roomId}, #{name}, #{price}, #{stock}, #{imageUrl})")
    void insert(LiveProduct liveProduct);

    @Select("select id, name, price, stock, image_url from live_product where room_id = #{roomId}")
    List<GetLiveProductResponse> selectByRoomId(Long roomId);

    @Select("select id, name, price, stock, image_url from live_product where id = #{id}")
    GetLiveProductResponse selectById(Long id);
}
