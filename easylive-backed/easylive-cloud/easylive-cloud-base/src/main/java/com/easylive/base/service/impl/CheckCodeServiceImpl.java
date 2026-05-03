package com.easylive.base.service.impl;

import cn.hutool.core.lang.UUID;

import com.easylive.base.entity.constants.Constants;
import com.easylive.base.exception.BusinessException;
import com.easylive.base.redis.RedisUtils;
import com.easylive.base.service.CheckCodeService;
import com.wf.captcha.ArithmeticCaptcha;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Service
public class CheckCodeServiceImpl implements CheckCodeService {

    private static final Logger log = LoggerFactory.getLogger(CheckCodeServiceImpl.class);
    @Resource
    private RedisUtils redisUtils;

    @Override
    public Map<String, String> generateCheckCode() {
        ArithmeticCaptcha captcha = new ArithmeticCaptcha(100, 42);
        String code = captcha.text();
        String checkCodeKey = UUID.randomUUID().toString();
        String redisKey = Constants.REDIS_KEY_CHECK_CODE + checkCodeKey;
        boolean stored = redisUtils.setex(redisKey, code, Constants.REDIS_TIME_1MIN * 10);
        if (!stored) {
            log.error("验证码存储失败: key={}", redisKey);
            throw new BusinessException("验证码生成失败，请重试");
        }
        log.info("生成验证码: key={}, code={}", redisKey, code);
        String checkCodeBase64 = captcha.toBase64();
        Map<String, String> result = new HashMap<>();
        result.put("checkCode", checkCodeBase64);
        result.put("checkCodeKey", checkCodeKey);
        return result;
    }

    @Override
    public boolean verifyCheckCode(String checkCodeKey, String checkCode) {
        if (checkCodeKey == null || checkCodeKey.isEmpty() || checkCode == null || checkCode.isEmpty()) {
            log.warn("校验参数为空: key={}, code={}", checkCodeKey, checkCode);
            return false;
        }
        String redisKey = Constants.REDIS_KEY_CHECK_CODE + checkCodeKey;
        Object value = redisUtils.get(redisKey);
        log.info("校验验证码: key={}, inputCode={}, storedValue={}, type={}", redisKey, checkCode, value, value != null ? value.getClass() : "null");
        if (value == null) {
            log.warn("验证码已过期或不存在: {}", redisKey);
            return false;
        }
        if (!(value instanceof String)) {
            log.error("Redis中存储的值不是String类型: {}", value);
            return false;
        }
        String correctCode = (String) value;
        boolean verifyResult = correctCode.equalsIgnoreCase(checkCode);
        if (verifyResult) {
            redisUtils.delete(redisKey);
            log.info("验证码校验成功，已删除: {}", redisKey);
        } else {
            log.warn("验证码不匹配: stored={}, input={}", correctCode, checkCode);
        }
        return verifyResult;
    }
}