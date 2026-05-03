package com.easylive.base.entity.dto;

import java.io.Serializable;

public class TokenUserInfoDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String token;
    private String userId;
    private String nickName;
    private Boolean admin;
    private String email;
    private String avatarPath;
    private Boolean Vip = false;          // VIP标识（true=VIP，false=普通用户）
    private Integer vipLevel;       // VIP等级（0=普通，1=VIP）
    private String vipLevelName;    // VIP等级名称（如"VIP会员"）
    private Long totalSpace;        // 用户总空间


    public Boolean getVip() {
        return Vip;
    }

    public void setVip(Boolean vip) {
        Vip = vip;
    }

    public Integer getVipLevel() {
        return vipLevel;
    }

    public void setVipLevel(Integer vipLevel) {
        this.vipLevel = vipLevel;
    }

    public Long getTotalSpace() {
        return totalSpace;
    }

    public void setTotalSpace(Long totalSpace) {
        this.totalSpace = totalSpace;
    }

    public String getVipLevelName() {
        return vipLevelName;
    }

    public void setVipLevelName(String vipLevelName) {
        this.vipLevelName = vipLevelName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatarPath() {
        return avatarPath;
    }

    public void setAvatarPath(String avatarPath) {
        this.avatarPath = avatarPath;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public Boolean getAdmin() {
        return admin;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }
}
