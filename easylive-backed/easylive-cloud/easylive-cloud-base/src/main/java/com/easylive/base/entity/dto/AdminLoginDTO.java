package com.easylive.base.entity.dto;

import lombok.Data;

@Data
public class AdminLoginDTO {
    private String userName;
    private String password;
    private String checkCodeKey;
    private String checkCode;
}