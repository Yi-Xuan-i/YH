package com.yixuan.yh.video.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yixuan.yh.video.pojo.entity.Video;
import com.yixuan.yh.video.pojo.entity.VideoTag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface VideoTagMapper extends BaseMapper<VideoTag> {

    List<String> selectNameByIds(List<Long> idList);

    void insertBatch(List<VideoTag> videoTagList);

    List<VideoTag> selectSimplyByNames(List<String> addedNewTagList);

    @Select("select name from video_tag")
    List<String> selectTagsName();

    @Update("update video set status = #{videoStatus} where id = #{id}")
    void updateStatus(Long id, Video.VideoStatus videoStatus);
}
