package com.yixuan.yh.video.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yixuan.yh.video.mapper.VideoTagMapper;
import com.yixuan.yh.video.mapper.VideoTagMpMapper;
import com.yixuan.yh.video.pojo.entity.multi.VideoTagMp;
import com.yixuan.yh.video.service.VideoTagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VideoTagServiceImpl implements VideoTagService {

    @Autowired
    private VideoTagMapper videoTagMapper;
    @Autowired
    private VideoTagMpMapper videoTagMpMapper;

    @Override
    public List<String> getSimpleVideoTags() {
        return videoTagMapper.selectTagsName();
    }

    @Override
    public List<Long> getVideoTags(Long videoId) {
        return videoTagMpMapper.selectList(new LambdaQueryWrapper<VideoTagMp>()
                .select(VideoTagMp::getTagId)
                .eq(VideoTagMp::getVideoId, videoId))
                .stream().map(VideoTagMp::getTagId).toList();
    }
}
