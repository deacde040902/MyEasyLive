package com.easylive.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easylive.base.entity.po.UserTheme;
import com.easylive.base.mapper.UserThemeMapper;
import com.easylive.base.service.UserThemeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class UserThemeServiceImpl extends ServiceImpl<UserThemeMapper, UserTheme> implements UserThemeService {

    @Override
    @Transactional
    public void saveTheme(String userId, String themeColor, Integer darkMode) {
        LambdaQueryWrapper<UserTheme> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserTheme::getUserId, userId);
        UserTheme exist = this.getOne(wrapper);
        if (exist == null) {
            exist = new UserTheme();
            exist.setUserId(userId);
            exist.setThemeColor(themeColor != null ? themeColor : "#FFFFFF");
            exist.setDarkMode(darkMode != null ? darkMode : 0);
            exist.setCreateTime(LocalDateTime.now());
            this.save(exist);
        } else {
            if (themeColor != null) exist.setThemeColor(themeColor);
            if (darkMode != null) exist.setDarkMode(darkMode);
            exist.setUpdateTime(LocalDateTime.now());
            this.updateById(exist);
        }
    }

    @Override
    public UserTheme getTheme(String userId) {
        LambdaQueryWrapper<UserTheme> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserTheme::getUserId, userId);
        UserTheme theme = this.getOne(wrapper);
        if (theme == null) {
            theme = new UserTheme();
            theme.setUserId(userId);
            theme.setThemeColor("#FFFFFF");
            theme.setDarkMode(0);
        }
        return theme;
    }
}