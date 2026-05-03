package com.easylive.base.entity.dto;



import com.easylive.base.entity.constants.Constants;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public class ChangePwdDTO {
    @NotBlank(message = "旧密码不能为空")
    private String oldPassword;

    @NotBlank(message = "新密码不能为空")
    @Pattern(regexp = Constants.REGEX_PASSWORD, message = "密码格式错误")
    private String newPassword;

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}