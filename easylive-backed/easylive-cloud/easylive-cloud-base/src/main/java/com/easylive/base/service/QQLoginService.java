package com.easylive.base.service;

import com.easylive.base.entity.po.UserInfo;
import java.util.Map;

/**
 * QQ登录服务
 */
public interface QQLoginService {
    
    /**
     * QQ登录
     * @param code QQ授权码
     * @return 登录结果，包含token和用户信息
     */
    Map<String, Object> qqLogin(String code);
    
    /**
     * 根据QQ openid获取用户信息
     * @param openid QQ openid
     * @return 用户信息
     */
    UserInfo getUserByOpenid(String openid);
    
    /**
     * 绑定QQ账号
     * @param userId 用户ID
     * @param openid QQ openid
     * @return 是否绑定成功
     */
    boolean bindQQ(String userId, String openid);
}
