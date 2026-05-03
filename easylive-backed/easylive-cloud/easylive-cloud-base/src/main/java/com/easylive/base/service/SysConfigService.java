package com.easylive.base.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easylive.base.entity.dto.SysSettingDto;
import com.easylive.base.entity.po.SysConfig;


import java.util.List;
import java.util.Map;

/**
 * <p>
 * 系统配置表 服务类
 * </p>
 *
 * @author licheng
 * @since 2026-03-24
 */
public interface SysConfigService extends IService<SysConfig> {

    /**
     * 查询所有系统配置（键值对形式）
     */
    Map<String, String> getSysConfigMap();

    /**
     * 保存系统配置（新增/更新）
     */
    void saveSysConfig(Map<String, String> configMap);

     Map<String, String> getSystemSettings();

     void saveSystemSettings(SysSettingDto settings);

    Map<String, Object> getActualTimeStatistics();
    List<Map<String, Object>> getWeekStatistics();
    void saveListSettings(String module, Map<String, Object> settings);


}