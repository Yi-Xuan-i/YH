package com.yixuan.yh.product.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yixuan.yh.product.mapper.SpecValueMapper;
import com.yixuan.yh.product.pojo.model.entity.SpecValue;
import com.yixuan.yh.product.service.SpecValueService;
import org.springframework.stereotype.Service;

@Service
public class SpecValueServiceImpl extends ServiceImpl<SpecValueMapper, SpecValue> implements SpecValueService {
}
