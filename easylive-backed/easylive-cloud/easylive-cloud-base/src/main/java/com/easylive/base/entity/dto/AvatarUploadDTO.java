package com.easylive.base.entity.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
import javax.validation.constraints.NotBlank;

/**
 * 头像上传 DTO
 */
@Data
public class AvatarUploadDTO {
    /**
     * 用户ID（必填）
     */
    @NotBlank(message = "用户ID不能为空")
    private String userId;

    /**
     * 头像文件（前端上传的文件）
     */
    private MultipartFile file;
}