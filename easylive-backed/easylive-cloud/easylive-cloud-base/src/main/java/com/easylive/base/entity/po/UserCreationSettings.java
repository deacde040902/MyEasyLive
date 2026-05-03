package com.easylive.base.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 创作中心用户设置表
 * </p>
 *
 * @author licheng
 * @since 2026-04-19
 */
@Getter
@Setter
@TableName("user_creation_settings")
public class UserCreationSettings implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @TableId(value = "user_id", type = IdType.ASSIGN_ID)
    private String userId;

    /**
     * 设置键
     */
    @TableField("setting_key")
    private String settingKey;

    /**
     * 设置值
     */
    @TableField("setting_value")
    private String settingValue;

    @TableField("update_time")
    private LocalDateTime updateTime;
}
