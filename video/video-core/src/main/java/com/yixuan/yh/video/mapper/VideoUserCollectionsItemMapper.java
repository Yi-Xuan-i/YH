package com.yixuan.yh.video.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yixuan.yh.video.pojo.entity.VideoUserCollectionsItem;
import com.yixuan.yh.video.pojo.entity.multi.VideoCollectionsWithVideo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface VideoUserCollectionsItemMapper extends BaseMapper<VideoUserCollectionsItem> {
    List<VideoCollectionsWithVideo> selectCollectionsItemList(Long collectionsId, Long lastMinId);
}
