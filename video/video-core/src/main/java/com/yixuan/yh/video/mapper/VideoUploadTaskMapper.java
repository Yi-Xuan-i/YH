package com.yixuan.yh.video.mapper;

import com.yixuan.yh.video.pojo.entity.VideoUploadTask;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface VideoUploadTaskMapper {
    @Insert("insert into video_upload_task (id, user_id, file_size, total_chunks, chunk_bitmap) values(#{id}, #{userId}, #{fileSize}, #{totalChunks}, #{chunkBitmap})")
    void insert(VideoUploadTask videoUploadTask);
}
