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
 * 用户信息
 * </p>
 *
 * @author licheng
 * @since 2026-04-17
 */
@Getter
@Setter
@TableName("user_info")
public class UserInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    @TableId(value = "user_id", type = IdType.ASSIGN_ID)
    private String userId;

    /**
     * 用户昵称
     */
    @TableField("nick_name")
    private String nickName;

    /**
     * 邮箱 
     */
    @TableField("email")
    private String email;

    /**
     * 头像存储路径
     */
    @TableField("avatar_path")
    private String avatarPath;

    /**
     * qqOpenId
     */
    @TableField("qq_open_id")
    private String qqOpenId;

    /**
     * qq头像
     */
    @TableField("qq_avatar")
    private String qqAvatar;

    /**
     * 密码
     */
    @TableField("password")
    private String password;

    /**
     * 登录时间
     */
    @TableField("login_time")
    private LocalDateTime loginTime;

    /**
     * 最后登录时间
     */
    @TableField("last_login_time")
    private LocalDateTime lastLoginTime;

    /**
     * 0:禁用 1：启用
     */
    @TableField("status")
    private Boolean status;

    /**
     * 使用空间单位 byte类型
     */
    @TableField("use_space")
    private Long useSpace;

    /**
     * 用户创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;
}
