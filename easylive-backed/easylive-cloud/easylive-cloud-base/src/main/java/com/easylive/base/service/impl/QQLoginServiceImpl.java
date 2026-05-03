package com.easylive.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.easylive.base.entity.po.UserInfo;
import com.easylive.base.service.QQLoginService;
import com.easylive.base.service.UserInfoService;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Map;
import java.util.UUID;

/**
 * QQ登录服务实现
 */
@Service
public class QQLoginServiceImpl implements QQLoginService {
    
    private static final String APP_ID = "your_qq_app_id";
    private static final String APP_KEY = "your_qq_app_key";
    private static final String REDIRECT_URI = "http://localhost:3000/login/qq";
    private static final String AUTHORIZATION_URL = "https://graph.qq.com/oauth2.0/token";
    private static final String OPENID_URL = "https://graph.qq.com/oauth2.0/me";
    private static final String USER_INFO_URL = "https://graph.qq.com/user/get_user_info";
    
    private final RestTemplate restTemplate = new RestTemplate();
    
    private final UserInfoService userInfoService;
    
    public QQLoginServiceImpl(UserInfoService userInfoService) {
        this.userInfoService = userInfoService;
    }
    
    @Override
    public Map<String, Object> qqLogin(String code) {
        try {
            // 1. 获取access_token
            String tokenUrl = AUTHORIZATION_URL + "?grant_type=authorization_code&client_id=" + APP_ID + "&client_secret=" + APP_KEY + "&code=" + code + "&redirect_uri=" + REDIRECT_URI;
            String tokenResponse = restTemplate.getForObject(tokenUrl, String.class);
            
            // 2. 解析access_token
            String accessToken = parseAccessToken(tokenResponse);
            
            // 3. 获取openid
            String openidUrl = OPENID_URL + "?access_token=" + accessToken;
            String openidResponse = restTemplate.getForObject(openidUrl, String.class);
            String openid = parseOpenid(openidResponse);
            
            // 4. 获取用户信息
            String userInfoUrl = USER_INFO_URL + "?access_token=" + accessToken + "&oauth_consumer_key=" + APP_ID + "&openid=" + openid;
            Map<?, ?> userInfoMap = restTemplate.getForObject(userInfoUrl, Map.class);
            Map<String, Object> userInfo = new java.util.HashMap<>();
            if (userInfoMap != null) {
                for (Map.Entry<?, ?> entry : userInfoMap.entrySet()) {
                    if (entry.getKey() instanceof String) {
                        userInfo.put((String) entry.getKey(), entry.getValue());
                    }
                }
            }
            
            // 5. 检查用户是否存在
            UserInfo existingUser = getUserByOpenid(openid);
            
            if (existingUser == null) {
                // 创建新用户
                existingUser = new UserInfo();
                existingUser.setUserId(UUID.randomUUID().toString());
                existingUser.setNickName((String) userInfo.get("nickname"));
                existingUser.setPassword(UUID.randomUUID().toString()); // 生成随机密码
                existingUser.setAvatarPath((String) userInfo.get("figureurl_qq_1"));
                existingUser.setQqOpenId(openid);
                userInfoService.save(existingUser);
            }
            
            // 6. 生成token
            // 这里需要实现生成token的逻辑，暂时返回模拟值
            String token = "mock-token-" + UUID.randomUUID().toString();
            
            // 7. 返回结果
            Map<String, Object> result = new java.util.HashMap<>();
            result.put("token", token);
            result.put("user", existingUser);
            return result;
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("QQ登录失败");
        }
    }
    
    @Override
    public UserInfo getUserByOpenid(String openid) {
        LambdaQueryWrapper<UserInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserInfo::getQqOpenId, openid);
        return userInfoService.getOne(wrapper);
    }
    
    @Override
    public boolean bindQQ(String userId, String openid) {
        UserInfo user = userInfoService.getById(userId);
        if (user != null) {
            user.setQqOpenId(openid);
            return userInfoService.updateById(user);
        }
        return false;
    }
    
    /**
     * 解析access_token
     */
    private String parseAccessToken(String response) {
        // 简单解析，实际项目中应该使用更健壮的解析方式
        String[] params = response.split("&");
        for (String param : params) {
            if (param.startsWith("access_token=")) {
                return param.substring(13);
            }
        }
        throw new RuntimeException("获取access_token失败");
    }
    
    /**
     * 解析openid
     */
    private String parseOpenid(String response) {
        // 简单解析，实际项目中应该使用更健壮的解析方式
        int start = response.indexOf("openid\":\"");
        if (start == -1) {
            throw new RuntimeException("获取openid失败");
        }
        start += 9;
        int end = response.indexOf("\"", start);
        if (end == -1) {
            throw new RuntimeException("获取openid失败");
        }
        return response.substring(start, end);
    }
}