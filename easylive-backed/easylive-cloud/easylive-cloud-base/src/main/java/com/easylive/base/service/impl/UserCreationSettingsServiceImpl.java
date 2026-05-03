package com.easylive.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.easylive.base.entity.po.UserCreationSettings;
import com.easylive.base.mapper.UserCreationSettingsMapper;
import com.easylive.base.service.UserCreationSettingsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserCreationSettingsServiceImpl extends ServiceImpl<UserCreationSettingsMapper, UserCreationSettings> implements UserCreationSettingsService {

    @Override
    public void saveSettings(String userId, Map<String, String> settings) {
        LambdaQueryWrapper<UserCreationSettings> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserCreationSettings::getUserId, userId);
        this.remove(wrapper);
        List<UserCreationSettings> list = settings.entrySet().stream()
                .map(entry -> {
                    UserCreationSettings setting = new UserCreationSettings();
                    setting.setUserId(userId);
                    setting.setSettingKey(entry.getKey());
                    setting.setSettingValue(entry.getValue());
                    setting.setUpdateTime(LocalDateTime.now());   // 修改此处
                    return setting;
                })
                .collect(Collectors.toList());
        this.saveBatch(list);
    }

    @Override
    public Map<String, String> getSettings(String userId) {
        LambdaQueryWrapper<UserCreationSettings> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserCreationSettings::getUserId, userId);
        List<UserCreationSettings> list = this.list(wrapper);
        return list.stream().collect(Collectors.toMap(
                UserCreationSettings::getSettingKey,
                UserCreationSettings::getSettingValue,
                (v1, v2) -> v2
        ));
    }
}