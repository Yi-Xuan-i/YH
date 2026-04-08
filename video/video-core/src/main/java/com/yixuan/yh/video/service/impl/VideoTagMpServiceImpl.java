package com.yixuan.yh.video.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yixuan.yh.video.mapper.VideoTagMpMapper;
import com.yixuan.yh.video.pojo.entity.multi.VideoTagMp;
import com.yixuan.yh.video.service.VideoTagMpService;
import org.springframework.stereotype.Service;

@Service
public class VideoTagMpServiceImpl extends ServiceImpl<VideoTagMpMapper, VideoTagMp> implements VideoTagMpService {
}
