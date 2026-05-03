package com.easylive.base.entity.dto;

import javax.validation.constraints.NotBlank;

public class LoginDTO {

    @NotBlank(message = "邮箱不能为空")
    private String email;

    @NotBlank(message = "密码不能为空")
    private String password;

    @NotBlank(message = "图片验证码不能为空")
    private String checkCode;

    @NotBlank(message = "验证码key不能为空")
    private String checkCodeKey;

    // getter setter
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getCheckCode() { return checkCode; }
    public void setCheckCode(String checkCode) { this.checkCode = checkCode; }
    public String getCheckCodeKey() { return checkCodeKey; }
    public void setCheckCodeKey(String checkCodeKey) { this.checkCodeKey = checkCodeKey; }
}