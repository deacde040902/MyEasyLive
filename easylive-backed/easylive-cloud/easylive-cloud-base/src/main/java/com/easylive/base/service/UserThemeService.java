package com.easylive.base.service;

import com.easylive.base.entity.po.UserTheme;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 用户主题设置表 服务类
 * </p>
 *
 * @author licheng
 * @since 2026-04-22
 */
public interface UserThemeService extends IService<UserTheme> {
    void saveTheme(String userId, String themeColor, Integer darkMode);
    UserTheme getTheme(String userId);
}
