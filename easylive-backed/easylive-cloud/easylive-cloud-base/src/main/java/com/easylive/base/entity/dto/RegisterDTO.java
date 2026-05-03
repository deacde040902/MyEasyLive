package com.easylive.base.entity.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * 注册请求DTO（适配现有验证码逻辑）
 */
public class RegisterDTO {
    // 邮箱（必填+格式校验）
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;

    // 昵称（2-16位）
    @NotBlank(message = "昵称不能为空")
    @Size(min = 2, max = 16, message = "昵称长度需在2-16位之间")
    private String nickName;

    // 密码（8-18位，数字+字母+特殊字符）
    @NotBlank(message = "密码不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9!@#$%^&*()_+]{8,18}$", message = "密码需为8-18位，包含数字、字母或特殊字符")
    private String password;

    // 图片验证码key（和生成验证码接口返回的checkCodeKey一致）
    @NotBlank(message = "验证码key不能为空")
    private String checkCodeKey;

    // 图片验证码（用户输入）
    @NotBlank(message = "图片验证码不能为空")
    private String checkCode;

    // 邮箱验证码（用户输入）
    @NotBlank(message = "邮箱验证码不能为空")
    private String emailCode;

    // getter/setter 方法
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getNickName() { return nickName; }
    public void setNickName(String nickName) { this.nickName = nickName; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getCheckCodeKey() { return checkCodeKey; }
    public void setCheckCodeKey(String checkCodeKey) { this.checkCodeKey = checkCodeKey; }
    public String getCheckCode() { return checkCode; }
    public void setCheckCode(String checkCode) { this.checkCode = checkCode; }
    public String getEmailCode() { return emailCode; }
    public void setEmailCode(String emailCode) { this.emailCode = emailCode; }
}