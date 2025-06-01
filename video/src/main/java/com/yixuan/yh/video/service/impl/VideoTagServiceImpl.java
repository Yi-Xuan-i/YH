package com.yixuan.yh.video.service.impl;

import com.yixuan.yh.video.mapper.VideoTagMapper;
import com.yixuan.yh.video.response.GetSimpleVideoTagResponse;
import com.yixuan.yh.video.service.VideoTagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VideoTagServiceImpl implements VideoTagService {

    @Autowired
    private VideoTagMapper videoTagMapper;

    @Override
    public List<GetSimpleVideoTagResponse> getSimpleVideoTags() {
        return videoTagMapper.selectSimply();
    }
}
