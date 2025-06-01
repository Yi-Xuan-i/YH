package com.yixuan.yh.video.mapper;

import com.yixuan.yh.video.entity.VideoTag;
import com.yixuan.yh.video.response.GetSimpleVideoTagResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface VideoTagMapper {

    List<String> selectNameByIds(List<Long> idList);

    void insertBatch(List<VideoTag> videoTagList);

    List<VideoTag> selectSimplyByNames(List<String> addedNewTagList);

    @Select("select id, name from video_tag")
    List<GetSimpleVideoTagResponse> selectSimply();
}
