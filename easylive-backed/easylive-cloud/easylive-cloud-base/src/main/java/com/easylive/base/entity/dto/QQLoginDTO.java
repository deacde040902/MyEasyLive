// QQ登录请求DTO
package com.easylive.base.entity.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class QQLoginDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private String callbackUrl; // 登录成功后前端回调地址（可选）
}

