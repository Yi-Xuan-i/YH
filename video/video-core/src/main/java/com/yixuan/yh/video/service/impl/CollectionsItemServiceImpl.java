package com.yixuan.yh.video.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yixuan.yh.video.mapper.VideoUserCollectionsItemMapper;
import com.yixuan.yh.video.pojo.entity.VideoUserCollectionsItem;
import com.yixuan.yh.video.service.CollectionsItemService;
import org.springframework.stereotype.Service;

@Service
public class CollectionsItemServiceImpl extends ServiceImpl<VideoUserCollectionsItemMapper, VideoUserCollectionsItem> implements CollectionsItemService {
}
