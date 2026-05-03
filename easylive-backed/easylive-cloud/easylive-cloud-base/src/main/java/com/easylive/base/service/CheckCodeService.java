package com.easylive.base.service;

import java.util.Map;

public interface CheckCodeService {
    /**
     * 生成验证码
     * @return 包含验证码Base64和验证码Key的Map
     */
    Map<String, String> generateCheckCode();

    /**
     * 校验验证码
     * @param checkCodeKey 验证码key
     * @param checkCode 待校验的验证码
     * @return 校验结果：true-成功，false-失败
     */
    boolean verifyCheckCode(String checkCodeKey, String checkCode);
}