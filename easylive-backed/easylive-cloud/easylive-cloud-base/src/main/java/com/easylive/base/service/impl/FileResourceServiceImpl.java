package com.easylive.base.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easylive.base.entity.po.FileResource;
import com.easylive.base.exception.BusinessException;
import com.easylive.base.mapper.FileResourceMapper;
import com.easylive.base.service.FileResourceService;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.time.LocalDateTime;

@Service
public class FileResourceServiceImpl extends ServiceImpl<FileResourceMapper, FileResource> implements FileResourceService {

    private static final Logger log = LoggerFactory.getLogger(FileResourceServiceImpl.class);

    @Value("${easylive.file.base-path}")
    private String fileBasePath;

    @Resource
    private FileResourceMapper fileResourceMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String uploadFile(MultipartFile file, String userId) {
        // 1. 参数校验
        if (file == null || file.isEmpty()) {
            throw new BusinessException("文件不能为空");
        }
        if (userId == null || userId.isEmpty()) {
            throw new BusinessException("用户ID不能为空");
        }

        // 2. 获取原始文件名和扩展名
        String originalName = file.getOriginalFilename();
        String suffix = originalName.substring(originalName.lastIndexOf(".") + 1).toLowerCase();

        // 3. 判断文件类型
        String fileType = "other";
        if (suffix.matches("jpg|jpeg|png|gif|bmp")) {
            fileType = "image";
        } else if (suffix.matches("mp4|avi|mov|flv|mkv")) {
            fileType = "video";
        }

        // 4. 生成存储路径（按日期分目录，避免单目录文件过多）
        String dateDir = LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String relativePath = dateDir + "/" + System.currentTimeMillis() + "_" + userId + "." + suffix;
        String fullPath = fileBasePath + File.separator + relativePath;

        // 5. 创建目录并保存文件
        File destDir = new File(fileBasePath + File.separator + dateDir);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        try {
            file.transferTo(new File(fullPath));
            log.info("文件上传成功: {}", fullPath);
        } catch (IOException e) {
            log.error("文件保存失败", e);
            throw new BusinessException("文件上传失败: " + e.getMessage());
        }

        // 6. 保存文件信息到数据库
        FileResource fileResource = new FileResource();
        fileResource.setUserId(userId);
        fileResource.setOriginalName(originalName);
        fileResource.setFilePath(relativePath);
        fileResource.setFileSize(file.getSize());
        fileResource.setFileType(fileType);
        fileResource.setMimeType(file.getContentType());
        fileResource.setStatus((byte) 1);
        fileResource.setCreateTime(LocalDateTime.now());
        
        this.save(fileResource);
        log.info("文件信息保存到数据库成功: fileId={}", fileResource.getFileId());
        
        return fileResource.getFileId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteFile(String fileId, String userId) {
        // 简化实现
    }

    @Override
    public void downloadFile(String fileId, HttpServletResponse response) {
        // 简化实现
    }

    @Override
    public FileResource getFileInfo(String fileId) {
        FileResource fileResource = this.getById(fileId);
        if (fileResource == null || fileResource.getStatus() == 0) {
            throw new BusinessException("文件不存在或已删除");
        }
        return fileResource;
    }
}