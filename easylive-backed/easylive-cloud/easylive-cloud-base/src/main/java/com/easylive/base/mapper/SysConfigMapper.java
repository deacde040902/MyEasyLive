package com.easylive.base.mapper;

import com.easylive.base.entity.po.SysConfig;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

/**
 * <p>
 * 系统配置表 Mapper 接口
 * </p>
 *
 * @author licheng
 * @since 2026-04-19
 */
public interface SysConfigMapper extends BaseMapper<SysConfig> {
    /**
     * 自定义查询：直接返回键值对Map（性能更优）
     */
    @Select("SELECT config_key, config_value FROM sys_config")
    Map<String, String> selectConfigMap();
}
