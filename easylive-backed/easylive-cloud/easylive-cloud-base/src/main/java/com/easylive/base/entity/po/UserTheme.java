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
 * 用户主题设置表
 * </p>
 *
 * @author licheng
 * @since 2026-04-22
 */
@Getter
@Setter
@TableName("user_theme")
public class UserTheme implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;
    private String userId;
    private String themeColor;
    private Integer darkMode;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
