package com.easylive.base.entity.dto;

import lombok.Data;

import java.io.Serializable;

// QQ回调请求DTO
@Data
public class QQCallbackDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private String code;  // QQ回调返回的授权码
    private String state; // 防CSRF的状态码
}
