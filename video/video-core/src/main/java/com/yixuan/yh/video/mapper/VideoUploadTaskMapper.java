package com.yixuan.yh.video.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yixuan.yh.video.pojo.entity.VideoUploadTask;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface VideoUploadTaskMapper extends BaseMapper<VideoUploadTask> {
    @Select("select id, user_id, `key`, status from video_upload_task where id = #{taskId}")
    VideoUploadTask selectForPresign(Long taskId);

    @Select("select id, user_id, `key`, total_chunks, upload_type, status from video_upload_task where id = #{taskId}")
    VideoUploadTask selectForComplete(Long taskId);
}
