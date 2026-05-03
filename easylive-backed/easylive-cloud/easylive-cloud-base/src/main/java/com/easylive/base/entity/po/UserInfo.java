package com.easylive.base.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户信息实体类（完全匹配数据库表结构 + 保留头像/创建时间功能）
 */
@Data
@TableName("user_info")
public class UserInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID（主键，非自增，UUID生成）
     */
    @TableId(type = IdType.ASSIGN_UUID)
    private String userId;

    /**
     * 用户昵称
     */
    private String nickName;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 头像路径（数据库字段avatar_path，头像功能核心）
     */
    private String avatarPath; // MP自动驼峰映射：avatarPath → avatar_path

    /**
     * QQ开放ID
     */
    private String qqOpenId;

    /**
     * QQ头像
     */
    private String qqAvatar;

    /**
     * 密码
     */
    private String password;

    /**
     * VIP等级（0-普通，1-VIP）
     */
    @TableField(exist = false)
    private Integer vipLevel = 0; // 默认普通用户

    /**
     * VIP过期时间
     */
    @TableField(exist = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date vipExpireTime;

    /**
     * 是否VIP（0-否，1-是）
     * 数据库字段is_vip，用注解强制映射（避免驼峰错误）
     */

    @TableField(value = "is_vip",exist = false)
    private Integer vip = 0; // 默认普通用户


    /**
     * 登录时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date loginTime;

    /**
     * 最后登录时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastLoginTime;

    /**
     * 状态：0-禁用，1-启用
     */
    private Integer status = 1; // 默认启用

    /**
     * 已使用空间（字节）
     */
    private Long useSpace = 0L; // 默认0

    /**
     * 创建时间（数据库字段create_time，基础字段）
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 空值处理：已用空间默认0，避免NPE
     */
    public Long getUseSpace() {
        return useSpace == null ? 0 : useSpace;
    }


    /**
     * 是否管理员（0-普通，1-管理员）
     */
    private Integer isAdmin = 0;

    /**
     * 是否大会员（0-否，1-是）
     */
    private Integer bigVipStatus = 0;

    /**
     * 大会员过期时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date bigVipExpireTime;

    /**
     * 大会员等级（1-月卡，2-季卡，3-年卡）
     */
    private Integer bigVipLevel = 0;

    // ========== 重写toString（完整输出所有字段，含头像/创建时间） ==========
    @Override
    public String toString() {
        return "UserInfo{" +
                "userId='" + userId + '\'' +
                ", nickName='" + nickName + '\'' +
                ", email='" + email + '\'' +
                ", avatarPath='" + avatarPath + '\'' +
                ", qqOpenId='" + qqOpenId + '\'' +
                ", qqAvatar='" + qqAvatar + '\'' +
                ", password='" + password + '\'' +
                ", vipLevel=" + vipLevel +
                ", vipExpireTime=" + vipExpireTime +
                ", isVip=" + vip +
                ", loginTime=" + loginTime +
                ", lastLoginTime=" + lastLoginTime +
                ", status=" + status +
                ", useSpace=" + useSpace +
                ", createTime=" + createTime +
                ", isAdmin=" + isAdmin +
                ", bigVipStatus=" + bigVipStatus +
                ", bigVipExpireTime=" + bigVipExpireTime +
                ", bigVipLevel=" + bigVipLevel +
                '}';
    }
}