package com.easylive.base.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.easylive.base.entity.dto.SysSettingDto;
import com.easylive.base.entity.po.Comment;
import com.easylive.base.entity.po.SysConfig;
import com.easylive.base.entity.po.UserInfo;
import com.easylive.base.entity.po.Video;
import com.easylive.base.mapper.SysConfigMapper;
import com.easylive.base.redis.RedisComponent;
import com.easylive.base.service.CommentService;
import com.easylive.base.service.SysConfigService;
import com.easylive.base.service.UserInfoService;
import com.easylive.base.service.VideoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 系统配置表 服务实现类
 * @author licheng
 * @since 2026-04-19
 */
@Service
public class SysConfigServiceImpl extends ServiceImpl<SysConfigMapper, SysConfig> implements SysConfigService {

    @Resource
    private UserInfoService  userInfoService;

    @Resource
    private VideoService videoService;

    @Resource
    private CommentService commentService;

    @Resource
    private RedisComponent  redisComponent;

    /**
     * 查询所有系统配置，返回键值对Map
     */
    @Override
    public Map<String, String> getSysConfigMap() {
        // 查询所有配置
        List<SysConfig> configList = this.list();
        Map<String, String> configMap = new HashMap<>(configList.size());

        // 转换为键值对格式
        for (SysConfig config : configList) {
            configMap.put(config.getConfigKey(), config.getConfigValue());
        }
        return configMap;
    }

    /**
     * 保存系统配置（存在则更新，不存在则新增）
     */
    @Override
    @Transactional(rollbackFor = Exception.class) // 添加事务控制，确保数据一致性
    public void saveSysConfig(Map<String, String> configMap) {
        for (Map.Entry<String, String> entry : configMap.entrySet()) {
            String configKey = entry.getKey();
            String configValue = entry.getValue();

            // 查询配置是否已存在
            LambdaQueryWrapper<SysConfig> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysConfig::getConfigKey, configKey);
            SysConfig existConfig = this.getOne(wrapper);

            if (existConfig != null) {
                // 存在则更新值
                existConfig.setConfigValue(configValue);
                this.updateById(existConfig);
            } else {
                // 不存在则新增
                SysConfig newConfig = new SysConfig();
                newConfig.setConfigKey(configKey);
                newConfig.setConfigValue(configValue);
                newConfig.setRemark("系统配置-" + configKey); // 补充默认备注
                this.save(newConfig);
            }
        }
    }

    @Override
    public Map<String, String> getSystemSettings() {
        Map<String, String> settings = new HashMap<>();
        List<SysConfig> configs = this.list();
        for (SysConfig config : configs) {
            settings.put(config.getConfigKey(), config.getConfigValue());
        }
        // 确保默认值
        if (!settings.containsKey("systemName")) {
            settings.put("systemName", "Easylive-仿bilibili在线视频观看网站");
        }
        if (!settings.containsKey("defaultSpace")) {
            settings.put("defaultSpace", "5");
        }
        if (!settings.containsKey("vipSpace")) {
            settings.put("vipSpace", "100");
        }
        return settings;
    }

    @Override
    public void saveSystemSettings(SysSettingDto settings) {
        // 保存系统名称
        saveConfig("systemName", settings.getSystemName());
        // 保存默认存储空间
        saveConfig("defaultSpace", settings.getDefaultSpace());
        // 保存VIP存储空间
        saveConfig("vipSpace", settings.getVipSpace());
    }

    private void saveConfig(String key, String value) {
        LambdaQueryWrapper<SysConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysConfig::getConfigKey, key);
        SysConfig config = this.getOne(wrapper);
        if (config == null) {
            config = new SysConfig();
            config.setConfigKey(key);
            config.setConfigValue(value);
            this.save(config);
        } else {
            config.setConfigValue(value);
            this.updateById(config);
        }
    }

    @Override
    public Map<String, Object> getActualTimeStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        // 用户总数
        stats.put("userCount", userInfoService.count());
        
        // 视频总数
        stats.put("videoCount", videoService.count());
        
        // 评论总数
        stats.put("commentCount", commentService.count());
        
        // 今日新增视频
        LocalDateTime todayStart = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime todayEnd = LocalDateTime.now();
        long todayVideoCount = videoService.count(new LambdaQueryWrapper<Video>()
                .between(Video::getCreateTime, todayStart, todayEnd));
        stats.put("todayVideoCount", todayVideoCount);
        
        return stats;
    }

    @Override
    public List<Map<String, Object>> getWeekStatistics() {
        List<Map<String, Object>> result = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            LocalDateTime start = LocalDateTime.now().minusDays(i).withHour(0).withMinute(0).withSecond(0);
            LocalDateTime end = start.plusDays(1);
            long newUsers = userInfoService.count(new LambdaQueryWrapper<UserInfo>()
                    .between(UserInfo::getCreateTime, start, end));
            long newVideos = videoService.count(new LambdaQueryWrapper<Video>()
                    .between(Video::getCreateTime, start, end));
            
            // 查询新增评论数
            long newComments = commentService.count(new LambdaQueryWrapper<Comment>()
                    .between(Comment::getCreateTime, start, end));
            
            Map<String, Object> day = new HashMap<>();
            day.put("date", start.toLocalDate().toString());
            day.put("newUsers", newUsers);
            day.put("newVideos", newVideos);
            day.put("newComments", newComments);
            result.add(day);
        }
        return result;
    }

    @Override
    public void saveListSettings(String module, Map<String, Object> settings) {
        // 存储到 Redis 或数据库
        redisComponent.set("list_settings:" + module, JSON.toJSONString(settings));
    }

}