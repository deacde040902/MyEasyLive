package com.easylive.base.service;

import com.easylive.base.entity.po.UserCreationSettings;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * <p>
 * 创作中心用户设置表 服务类
 * </p>
 *
 * @author licheng
 * @since 2026-04-19
 */
public interface UserCreationSettingsService extends IService<UserCreationSettings> {
    void saveSettings(String userId, Map<String, String> settings);
    Map<String, String> getSettings(String userId);
}
