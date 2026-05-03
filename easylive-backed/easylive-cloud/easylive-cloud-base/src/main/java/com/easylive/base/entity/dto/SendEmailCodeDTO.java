package com.easylive.base.entity.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * 发送邮箱验证码请求DTO
 */
public class SendEmailCodeDTO {
    // 邮箱格式校验 + 非空校验
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;

    // 验证码key非空校验
    @NotBlank(message = "验证码key不能为空")
    private String checkCodeKey;

    // 图片验证码非空校验
    @NotBlank(message = "图片验证码不能为空")
    private String checkCode;

    // 类型只能是0或1
    @NotBlank(message = "验证码类型不能为空")
    @Pattern(regexp = "^[01]$", message = "验证码类型错误（仅支持0/1）")
    private String type;

    // getter/setter
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getCheckCodeKey() { return checkCodeKey; }
    public void setCheckCodeKey(String checkCodeKey) { this.checkCodeKey = checkCodeKey; }
    public String getCheckCode() { return checkCode; }
    public void setCheckCode(String checkCode) { this.checkCode = checkCode; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}