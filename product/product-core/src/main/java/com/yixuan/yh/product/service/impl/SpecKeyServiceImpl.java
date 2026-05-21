package com.yixuan.yh.product.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yixuan.yh.product.mapper.SpecKeyMapper;
import com.yixuan.yh.product.pojo.model.entity.SpecKey;
import com.yixuan.yh.product.service.SpecKeyService;
import org.springframework.stereotype.Service;

@Service
public class SpecKeyServiceImpl extends ServiceImpl<SpecKeyMapper, SpecKey> implements SpecKeyService {
}
