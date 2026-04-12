package com.yixuan.yh.videoprocessor.serivce.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yixuan.yh.videoprocessor.entity.UserVideoInteraction;
import com.yixuan.yh.videoprocessor.mapper.UserVideoInteractionMapper;
import com.yixuan.yh.videoprocessor.serivce.UserVideoInteractionService;
import org.springframework.stereotype.Service;

@Service
public class UserVideoInteractionServiceImpl extends ServiceImpl<UserVideoInteractionMapper, UserVideoInteraction> implements UserVideoInteractionService {
}
