package com.easylive.base.service;

public interface EmailService {
    /**
     * 发送邮箱验证码
     * @param email 目标邮箱
     * @param type 业务类型
     */
    void sendEmailCode(String email, String type);

    boolean verifyEmailCode(String email, String type, String inputCode);
}