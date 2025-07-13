package com.yixuan.yh.user.service;

public interface UserPreferencesService {

    void initUserPreferences(Long userId);

    float[] getUserVideoPreferences(Long userId);
}
