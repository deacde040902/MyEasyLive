package com.easylive.resource.controller;

import com.easylive.base.annotation.GlobalInterceptor;
import com.easylive.base.controller.ABaseController;
import com.easylive.base.entity.po.FileResource;
import com.easylive.base.entity.vo.ResponseVO;
import com.easylive.base.exception.BusinessException;
import com.easylive.base.redis.RedisComponent;
import com.easylive.base.service.FileResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
@RestController
@RequestMapping("/file")
public class FileResourceController extends ABaseController {

    @Autowired
    private FileResourceService fileResourceService;

    @Autowired
    private RedisComponent redisComponent;

    @Value("${easylive.file.base-path}")
    private String fileBasePath;

    @Value("${gateway.host:http://localhost:7071}")
    private String gatewayHost;

    /**
     * 从token获取用户ID
     */
    private String getUserIdByToken(String token) {
        if (!StringUtils.hasText(token)) {
            return null;
        }
        try {
            return redisComponent.getTokenUserInfoDto(token).getUserId();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 文件上传
     * @param file 文件
     * @param token 登录token
     * @return 文件ID
     */
    @PostMapping("/upload")
    @GlobalInterceptor
    public ResponseVO<String> uploadFile(@RequestParam("file") MultipartFile file,
                                 @RequestHeader("token") String token) {
        // 从token获取用户ID
        String userId = getUserIdByToken(token);
        if (!StringUtils.hasText(userId)) {
            throw new BusinessException("登录已过期，请重新登录");
        }
        String fileId = fileResourceService.uploadFile(file, userId);
        return getSuccessResponseVO(fileId);
    }

    /**
     * 获取文件信息
     * @param fileId 文件ID
     * @param token 登录token
     * @return 文件信息
     */
    @GetMapping("/info/{fileId}")
    @GlobalInterceptor
    public ResponseVO<FileResource> getFileInfo(@PathVariable("fileId") String fileId,
                                  @RequestHeader("token") String token) {
        // 验证token有效性并获取用户ID
        String userId = getUserIdByToken(token);
        if (!StringUtils.hasText(userId)) {
            throw new BusinessException("登录已过期，请重新登录");
        }
        FileResource fileInfo = fileResourceService.getFileInfo(fileId);
        // 权限校验：只能查看自己的文件
        if (!fileInfo.getUserId().equals(userId)) {
            throw new BusinessException("无权限查看此文件");
        }
        return getSuccessResponseVO(fileInfo);
    }

    /**
     * 下载文件（返回文件流）
     * @param fileId 文件ID
     * @param token 登录token
     * @return 文件流
     */
    @GetMapping("/download/{fileId}")
    @GlobalInterceptor
    public ResponseEntity<org.springframework.core.io.Resource> downloadFile(@PathVariable("fileId") String fileId,
                                                 @RequestHeader("token") String token) {
        // 验证token并获取用户ID
        String userId = getUserIdByToken(token);
        if (!StringUtils.hasText(userId)) {
            throw new BusinessException("登录已过期，请重新登录");
        }
        FileResource fileResource = fileResourceService.getFileInfo(fileId);
        // 权限校验：只能下载自己的文件
        if (!fileResource.getUserId().equals(userId)) {
            throw new BusinessException("无权限下载此文件");
        }
        File file = new File(fileBasePath, fileResource.getFilePath());
        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }
        org.springframework.core.io.Resource resource = new FileSystemResource(file);
        String contentType = determineContentType(file.getName());
        String encodedFileName = encodeFileName(fileResource.getOriginalName());
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + encodedFileName + "\"; filename*=UTF-8''" + encodedFileName)
                .body(resource);
    }

    /**
     * 获取文件访问URL（通过网关）
     * @param fileId 文件ID
     * @param token 登录token
     * @return 文件访问URL
     */
    @GetMapping("/url/{fileId}")
    @GlobalInterceptor
    public ResponseVO<String> getFileUrl(@PathVariable("fileId") String fileId,
                                 @RequestHeader("token") String token) {
        // 验证token
        String userId = getUserIdByToken(token);
        if (!StringUtils.hasText(userId)) {
            throw new BusinessException("登录已过期，请重新登录");
        }
        // 检查文件是否存在且属于当前用户（可选，也可以不校验直接返回URL）
        FileResource fileResource = fileResourceService.getFileInfo(fileId);
        if (!fileResource.getUserId().equals(userId)) {
            throw new BusinessException("无权限获取此文件URL");
        }
        // 构造网关下载地址
        String url = gatewayHost + "/resource/file/download/" + fileId;
        return getSuccessResponseVO(url);
    }

    /**
     * 根据文件扩展名确定Content-Type
     */
    private String determineContentType(String fileName) {
        if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (fileName.endsWith(".png")) {
            return "image/png";
        } else if (fileName.endsWith(".gif")) {
            return "image/gif";
        } else if (fileName.endsWith(".mp4")) {
            return "video/mp4";
        } else if (fileName.endsWith(".m3u8")) {
            return "application/vnd.apple.mpegurl";
        } else if (fileName.endsWith(".ts")) {
            return "video/MP2T";
        } else {
            return "application/octet-stream";
        }
    }

    /**
     * 编码文件名，支持中文文件名在HTTP响应头中正确传输
     */
    private String encodeFileName(String fileName) {
        try {
            return URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString())
                    .replace("+", "%20");
        } catch (Exception e) {
            return fileName;
        }
    }

    /**
     * 删除文件（物理删除文件及数据库记录）
     * @param fileId 文件ID
     * @param token 登录token
     * @return 操作结果
     */
    @DeleteMapping("/delete/{fileId}")
    @GlobalInterceptor
    public ResponseVO<Void> deleteFile(@PathVariable("fileId") String fileId,
                                       @RequestHeader("token") String token) {
        // 验证token并获取用户ID
        String userId = getUserIdByToken(token);
        if (!StringUtils.hasText(userId)) {
            throw new BusinessException("登录已过期，请重新登录");
        }
        // 调用Service删除文件
        fileResourceService.deleteFile(fileId, userId);
        return getSuccessResponseVO(null);
    }

    /**
     * 获取文件访问URL（无需登录，用于视频封面等公开资源）
     * @param fileId 文件ID
     */
    @GetMapping("/public/url/{fileId}")
    public ResponseVO<String> getPublicFileUrl(@PathVariable("fileId") String fileId) {
        FileResource fileResource = fileResourceService.getById(fileId);
        if (fileResource == null || fileResource.getStatus() != 1) {
            throw new BusinessException("文件不存在");
        }
        // 构造网关下载地址（去掉token验证）
        String url = gatewayHost + "/resource/file/public/download/" + fileId;
        return getSuccessResponseVO(url);
    }

    /**
     * 公开文件下载（无需登录）
     * @param fileId 文件ID
     */
    @GetMapping("/public/download/{fileId}")
    public ResponseEntity<org.springframework.core.io.Resource> publicDownload(@PathVariable("fileId") String fileId) {
        FileResource fileResource = fileResourceService.getById(fileId);
        if (fileResource == null || fileResource.getStatus() != 1) {
            return ResponseEntity.notFound().build();
        }
        File file = new File(fileBasePath, fileResource.getFilePath());
        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }
        org.springframework.core.io.Resource resource = new FileSystemResource(file);
        String contentType = determineContentType(file.getName());
        String encodedFileName = encodeFileName(fileResource.getOriginalName());
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + encodedFileName + "\"; filename*=UTF-8''" + encodedFileName)
                .body(resource);
    }
}