package com.easylive.base.service;

import com.easylive.base.entity.po.FileResource;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 * 文件资源表 服务类
 * </p>
 *
 * @author licheng
 * @since 2026-04-19
 */
public interface FileResourceService extends IService<FileResource> {
    /**
     * 上传文件
     * @param file 文件
     * @param userId 用户ID
     * @return 文件ID
     */
    String uploadFile(MultipartFile file, String userId);

    /**
     * 获取文件信息
     * @param fileId 文件ID
     * @return 文件实体
     */
    FileResource getFileInfo(String fileId);

    /**
     * 删除文件（物理删除）
     * @param fileId 文件ID
     * @param userId 当前用户ID（用于权限校验）
     */
    void deleteFile(String fileId, String userId);

    void downloadFile(String fileId, HttpServletResponse response);
}
