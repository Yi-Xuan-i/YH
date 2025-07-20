package com.yixuan.yh.user.service;

public interface PreferencesService {

    void initUserPreferences(Long userId);

    float[] getUserVideoPreferences(Long userId);
}
