package com.easylive.base.service.impl;


import com.easylive.base.entity.constants.Constants;
import com.easylive.base.entity.dto.StringTools;
import com.easylive.base.redis.RedisUtils;


import com.easylive.base.service.EmailService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Random;

@Service
public class EmailServiceImpl implements EmailService {

    @Resource
    private JavaMailSender mailSender;
    @Resource
    private RedisUtils redisUtils;

    private static final int CODE_LENGTH = 6;
    private static final String FROM_EMAIL = "2822867926@qq.com";

    /**
     * 发送邮箱验证码（修复Key规则：easychat:email:code:邮箱:类型）
     */
    @Override
    public void sendEmailCode(String email, String type) {
        // 1. 生成验证码
        String code = generateRandomCode();
        // 2. 构建Redis Key（和验证时完全一致）
        String redisKey = Constants.REDIS_KEY_EMAIL_CODE + email + ":" + type;
        // 3. 存储验证码（5分钟过期，和常量一致）
        redisUtils.setex(redisKey, code, Constants.REDIS_TIME_5MIN);
        // 打印日志（关键：用于排查Key是否匹配）
        System.out.println("📧 存储邮箱验证码：Key=" + redisKey + ", Code=" + code);
        // 4. 发送邮件
        sendEmail(email, type, code);
    }

    /**
     * 验证邮箱验证码（修复Key规则，和发送时一致）
     */
    @Override
    public boolean verifyEmailCode(String email, String type, String inputCode) {
        // 1. 参数校验
        if (StringTools.isEmpty(email) || StringTools.isEmpty(type) || StringTools.isEmpty(inputCode)) {
            return false;
        }
        // 2. 构建Redis Key（和发送时完全一致）
        String redisKey = Constants.REDIS_KEY_EMAIL_CODE + email + ":" + type;
        // 3. 获取Redis中的验证码
        String correctCode = (String) redisUtils.get(redisKey);
        // 打印日志（关键：排查验证码是否存在）
        System.out.println("验证邮箱验证码：Key=" + redisKey + ", 缓存Code=" + correctCode + ", 输入Code=" + inputCode);

        // 4. 验证码不存在（过期/未发送）
        if (correctCode == null) {
            return false;
        }
        // 5. 验证通过后删除
        boolean isValid = correctCode.equals(inputCode);
        if (isValid) {
            redisUtils.delete(redisKey);
            System.out.println("邮箱验证码验证通过，已删除Key=" + redisKey);
        }
        return isValid;
    }

    // 生成6位随机验证码
    private String generateRandomCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }

    // 发送邮件
    private void sendEmail(String to, String type, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(FROM_EMAIL);
        message.setTo(to);
        message.setSubject("1".equals(type) ? "EasyLive 找回密码验证码" : "EasyLive 注册验证码");
        message.setText("您的EasyLive验证码是：" + code + "，有效期5分钟，请尽快使用。");
        mailSender.send(message);
    }
}