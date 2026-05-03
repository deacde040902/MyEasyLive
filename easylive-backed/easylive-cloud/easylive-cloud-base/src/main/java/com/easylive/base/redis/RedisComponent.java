package com.easylive.base.redis;


import com.easylive.base.entity.constants.Constants;
import com.easylive.base.entity.dto.StringTools;
import com.easylive.base.entity.dto.SysSettingDto;
import com.easylive.base.entity.dto.TokenUserInfoDto;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

/**
 * Redis 业务组件（适配你的 RedisUtils）
 */
@Component("redisComponent")
public class RedisComponent {
    @Resource
    private RedisUtils redisUtils; // 泛型指定为 Object，兼容所有类型


    public Long getUserHeartBeat(String userId) {
        String key = Constants.REDIS_KEY_WS_HEART_BEAT + userId;
        Object value = redisUtils.get(key);
        return value == null ? null : (Long) value;
    }

    public void saveUserHeartBeat(String userId) {
        String key = Constants.REDIS_KEY_WS_HEART_BEAT + userId;
        // 使用 setex 存入并设置过期时间
        redisUtils.setex(key, System.currentTimeMillis(), Constants.REDIS_KEY_EXPIRES_HEART_BEAT);
    }

    public void removeUserHeartBeat(String userId) {
        String key = Constants.REDIS_KEY_WS_HEART_BEAT + userId;
        redisUtils.delete(key); // 适配 RedisUtils 的 delete 方法
    }

    public void saveTokenUserInfoDto(TokenUserInfoDto tokenUserInfoDto) {
        if (tokenUserInfoDto == null || StringTools.isEmpty(tokenUserInfoDto.getToken())
                || StringTools.isEmpty(tokenUserInfoDto.getUserId())) {
            throw new RuntimeException("Token或用户ID不能为空");
        }
        // 1. Token -> 用户信息（2天过期）
        String tokenKey = Constants.REDIS_KEY_WS_TOKEN + tokenUserInfoDto.getToken();
        redisUtils.setex(tokenKey, tokenUserInfoDto, Constants.REDIS_KEY_EXPIRES_DAY * 2);

        // 2. 用户ID -> Token（2天过期）
        String userIdKey = Constants.REDIS_KEY_WS_TOKEN_USERID + tokenUserInfoDto.getUserId();
        redisUtils.setex(userIdKey, tokenUserInfoDto.getToken(), Constants.REDIS_KEY_EXPIRES_DAY * 2);
    }

    public TokenUserInfoDto getTokenUserInfoDto(String token) {
        if (StringTools.isEmpty(token)) {
            return null;
        }
        String tokenKey = Constants.REDIS_KEY_WS_TOKEN + token;
        Object value = redisUtils.get(tokenKey);
        return value == null ? null : (TokenUserInfoDto) value;
    }

    public String getTokenByUserId(String userId) {
        if (StringTools.isEmpty(userId)) {
            return null;
        }
        String userIdKey = Constants.REDIS_KEY_WS_TOKEN_USERID + userId;
        Object value = redisUtils.get(userIdKey);
        return value == null ? null : (String) value;
    }

    public void cleanUserTokenByUserId(String userId) {
        String token = getTokenByUserId(userId);
        if (StringTools.isEmpty(token)) {
            return;
        }
        // 删除 Token 相关的两个 Key
        redisUtils.delete(Constants.REDIS_KEY_WS_TOKEN + token);
        redisUtils.delete(Constants.REDIS_KEY_WS_TOKEN_USERID + userId);
    }

    public SysSettingDto getSysSetting() {
        return getSysSettingDto();
    }

    public SysSettingDto getSysSettingDto() {
        Object value = redisUtils.get(Constants.REDIS_KEY_SYS_SETTING);
        SysSettingDto sysSettingDto = value == null ? new SysSettingDto() : (SysSettingDto) value;
        return sysSettingDto;
    }

    public void saveSysSetting(SysSettingDto sysSettingDto) {
        redisUtils.set(Constants.REDIS_KEY_SYS_SETTING, sysSettingDto); // 无过期时间
    }

    // ========== 联系人/关联数据相关 ==========
    public void delContactCache(String userId, String contactId) {
        String singleContactKey = Constants.REDIS_KEY_CONTACT_USER + userId + "_" + contactId;
        String contactListKey = Constants.REDIS_KEY_CONTACT_LIST + userId;
        // 批量删除两个 Key
        redisUtils.delete(singleContactKey, contactListKey);
    }

    public void cleanUserContact(String userId) {
        redisUtils.delete(Constants.REDIS_KEY_USER_CONTACT + userId);
    }

    public void addUserContactBatch(String userId, List<String> contactList) {
        String key = Constants.REDIS_KEY_USER_CONTACT + userId;
        // 将 List<String> 转为 List<Object> 适配 RedisUtils<Object>
        List<Object> objList = contactList.stream()
                .map(obj -> (Object) obj)
                .collect(java.util.stream.Collectors.toList());
        redisUtils.lpushAll(key, objList, Constants.REDIS_KEY_TOKEN_EXPIRES);
    }
    public void addUserContact(String userId, String contactId) {
        List<String> contactIdList = getUserContactList(userId);
        if (contactIdList.contains(contactId)) {
            return;
        }
        String key = Constants.REDIS_KEY_USER_CONTACT + userId;
        redisUtils.lpush(key, contactId, Constants.REDIS_KEY_TOKEN_EXPIRES);
    }

    public List<String> getUserContactList(String userId) {
        String key = Constants.REDIS_KEY_USER_CONTACT + userId;
        List<Object> list = redisUtils.getQueueList(key);
        // 替换 Stream.toList() 为 Collectors.toList()，兼容 JDK 8+
        return list == null ? Collections.emptyList() : list.stream()
                .map(String::valueOf)
                .collect(java.util.stream.Collectors.toList());
    }

    public void removeUserContact(String userId, String contactId) {
        String key = Constants.REDIS_KEY_USER_CONTACT + userId;
        redisUtils.remove(key, contactId);
    }
    /**
     * 根据Token直接删除Redis中的登录态（退出登录核心方法）
     * 兼容：WS_TOKEN（WebSocket/QQ登录） + 普通TOKEN（账号密码登录）双前缀
     * @param token 登录Token（可为空，空则不执行）
     */
    public void deleteTokenUserInfoDto(String token) {
        if (StringTools.isEmpty(token)) {
            return;
        }
        // 1. 删除WS_TOKEN前缀的Token记录
        String wsTokenKey = Constants.REDIS_KEY_WS_TOKEN + token;
        // 2. 删除普通TOKEN前缀的记录 --常规登录Key
        String normalTokenKey = Constants.REDIS_KEY_TOKEN + token;
        // 3. 批量删除两个Key，确保所有登录场景的Token彻底失效
        redisUtils.delete(wsTokenKey, normalTokenKey);
    }

    /**
     * 普通缓存放入（无过期时间）
     */
    public void set(String key, Object value) {
        redisUtils.set(key, value);
    }
    public RedisUtils getRedisUtils() {
        return this.redisUtils;
    }
    /**
     * 保存QQ登录的state（防CSRF）
     */
    public void setex(String key, Object value, int expireSeconds) {
        redisUtils.setex(key, value, expireSeconds);
    }

    /**
     * 获取QQ登录的state
     */
    public Object get(String key) {
        return redisUtils.get(key);
    }
}