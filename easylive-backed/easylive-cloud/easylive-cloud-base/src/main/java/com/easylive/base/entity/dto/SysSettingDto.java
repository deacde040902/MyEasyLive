package com.easylive.base.entity.dto;

import java.io.Serializable;

/**
 * 系统配置 DTO
 * 用于缓存和传输系统级配置信息
 */
public class SysSettingDto implements Serializable {
    private static final long serialVersionUID = 1L;

    // 文件上传相关配置
    private Long maxFileSize; // 单个文件最大上传大小（字节）
    private String allowedFileTypes; // 允许上传的文件类型（逗号分隔）

    // 头像相关配置
    private String defaultAvatarPath; // 默认头像路径
    private Long maxAvatarSize; // 头像最大上传大小（字节）

    // 邮箱相关配置
    private String emailFrom; // 发件人邮箱
    private String emailTitlePrefix; // 邮箱验证码标题前缀

    private String systemName;
    private String defaultSpace;
    private String vipSpace;

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    public String getDefaultSpace() {
        return defaultSpace;
    }

    public void setDefaultSpace(String defaultSpace) {
        this.defaultSpace = defaultSpace;
    }

    public String getVipSpace() {
        return vipSpace;
    }

    public void setVipSpace(String vipSpace) {
        this.vipSpace = vipSpace;
    }

    // ======== getter/setter 方法 ========
    public Long getMaxFileSize() {
        return maxFileSize;
    }

    public void setMaxFileSize(Long maxFileSize) {
        this.maxFileSize = maxFileSize;
    }

    public String getAllowedFileTypes() {
        return allowedFileTypes;
    }

    public void setAllowedFileTypes(String allowedFileTypes) {
        this.allowedFileTypes = allowedFileTypes;
    }

    public String getDefaultAvatarPath() {
        return defaultAvatarPath;
    }

    public void setDefaultAvatarPath(String defaultAvatarPath) {
        this.defaultAvatarPath = defaultAvatarPath;
    }

    public Long getMaxAvatarSize() {
        return maxAvatarSize;
    }

    public void setMaxAvatarSize(Long maxAvatarSize) {
        this.maxAvatarSize = maxAvatarSize;
    }

    public String getEmailFrom() {
        return emailFrom;
    }

    public void setEmailFrom(String emailFrom) {
        this.emailFrom = emailFrom;
    }

    public String getEmailTitlePrefix() {
        return emailTitlePrefix;
    }

    public void setEmailTitlePrefix(String emailTitlePrefix) {
        this.emailTitlePrefix = emailTitlePrefix;
    }
}