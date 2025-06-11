package com.yixuan.yh.live.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yixuan.yh.live.entity.Live;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface LiveMapper extends BaseMapper<Live> {

    @Update("update live set client_id = #{clientId} where room_id = #{roomId}")
    void updateClientIdById(Long roomId, String clientId);

    @Select("select anchor_id from live where room_id = #{roomId}")
    Long selectAnchorIdById(Long roomId);

    @Select("select client_id from live where room_id = #{roomId}")
    String selectClientIdById(Long roomId);
}
