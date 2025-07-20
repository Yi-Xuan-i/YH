package com.yixuan.yh.user.service.impl;

import com.yixuan.yh.common.utils.SnowflakeUtils;
import com.yixuan.yh.user.mapper.PreferencesMapper;
import com.yixuan.yh.user.pojo.entity.UserPreferences;
import com.yixuan.yh.user.service.PreferencesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.ByteBuffer;

@Service
public class PreferencesServiceImpl implements PreferencesService {

    @Autowired
    private PreferencesMapper preferencesMapper;
    @Autowired
    private SnowflakeUtils snowflakeUtils;

    @Override
    public void initUserPreferences(Long userId) {
        UserPreferences userPreferences = new UserPreferences();
        userPreferences.setId(snowflakeUtils.nextId());
        userPreferences.setUserId(userId);
        byte[] vector = new byte[800];
        userPreferences.setVideoPrefVector(vector);

        preferencesMapper.insert(userPreferences);
    }

    @Override
    public float[] getUserVideoPreferences(Long userId) {
        byte[] preferencesBytes = preferencesMapper.selectVideoPrefVectorByUserId(userId).get(0);
        ByteBuffer buffer = ByteBuffer.wrap(preferencesBytes);
        float[] vector = new float[200];
        for (int i = 0; i < 200; i++) {
            vector[i] = buffer.getFloat();
        }
        return vector;
    }
}
