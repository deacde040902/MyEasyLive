package com.easylive.admin.controller;

import com.easylive.base.annotation.GlobalInterceptor;
import com.easylive.base.controller.ABaseController;
import com.easylive.base.entity.dto.AdminLoginDTO;
import com.easylive.base.entity.dto.SysSettingDto;
import com.easylive.base.entity.po.*;
import com.easylive.base.entity.vo.ResponseVO;
import com.easylive.base.exception.BusinessException;
import com.easylive.base.redis.RedisComponent;
import com.easylive.base.service.*;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController("adminController")
@RequestMapping("/admin")
public class AdminController extends ABaseController {

    @Resource
    private CheckCodeService checkCodeService;
    @Resource
    private UserInfoService userInfoService;
    @Resource
    private RedisComponent redisComponent;
    @Resource
    private SysConfigService sysConfigService;
    @Resource
    private VideoCategoryService videoCategoryService;
    @Resource
    private FileResourceService fileResourceService;
    @Resource
    private VideoService videoService;
    @Resource
    private DanmuService danmuService;
    @Resource
    private CommentService commentService;
    /**
     * 生成验证码（管理员登录用）
     */
    @GetMapping("/checkCode")
    public ResponseVO<Map<String, String>> checkCode() {
        Map<String, String> result = checkCodeService.generateCheckCode();
        return getSuccessResponseVO(result);
    }

    /**
     * 管理员登录
     * 参数：userName（用户名或邮箱）, password, checkCodeKey, checkCode
     */
    @PostMapping("/login")
    public ResponseVO<Map<String, String>> login(@RequestBody AdminLoginDTO loginDTO) {
        // 1. 校验图片验证码
        boolean checkCodeValid = checkCodeService.verifyCheckCode(loginDTO.getCheckCodeKey(), loginDTO.getCheckCode());
        if (!checkCodeValid) {
            throw new BusinessException("图片验证码错误或已过期");
        }
        // 2. 登录（需校验是否为管理员）
        Map<String, String> result = userInfoService.adminLogin(loginDTO.getUserName(), loginDTO.getPassword());
        return getSuccessResponseVO(result);
    }

    /**
     * 管理员退出登录
     */
    @PostMapping("/logout")
    @GlobalInterceptor(checkAdmin = true)
    public ResponseVO<Void> logout(HttpServletRequest request) {
        String token = request.getHeader("token");
        if (StringUtils.hasText(token)) {
            redisComponent.deleteTokenUserInfoDto(token);
            // 可选：删除其他关联缓存
        }
        return getSuccessResponseVO(null);
    }

    /**
     * 获取系统设置（已存在，此处仅作示例）
     */
    @GetMapping("/settings")
    @GlobalInterceptor(checkAdmin = true)
    public ResponseVO<Map<String, String>> getSystemSettings() {
        Map<String, String> settings = sysConfigService.getSystemSettings();
        return getSuccessResponseVO(settings);
    }

    /**
     * 保存系统设置
     */
    @PostMapping("/settings")
    @GlobalInterceptor(checkAdmin = true)
    public ResponseVO<Void> saveSystemSettings(@RequestBody SysSettingDto settings) {
        sysConfigService.saveSystemSettings(settings);
        return getSuccessResponseVO(null);
    }

    // ==================== 视频分类管理 ====================

    /**
     * 获取分类列表（树形或列表）
     */
    @PostMapping("/category/list")
    @GlobalInterceptor(checkAdmin = true)
    public ResponseVO<List<VideoCategory>> getCategoryList() {
        List<VideoCategory> list = videoCategoryService.getCategoryList();
        return getSuccessResponseVO(list);
    }

    /**
     * 保存分类（新增或修改）
     * 参数：categoryId（可选）, categoryName, parentId, sortOrder
     */
    @PostMapping("/category/save")
    @GlobalInterceptor(checkAdmin = true)
    public ResponseVO<Void> saveCategory(@RequestBody VideoCategory category) {
        videoCategoryService.saveCategory(category);
        return getSuccessResponseVO(null);
    }

    /**
     * 删除分类
     * 参数：categoryId
     */
    @PostMapping("/category/delete")
    @GlobalInterceptor(checkAdmin = true)
    public ResponseVO<Void> deleteCategory(@RequestParam Integer categoryId) {
        videoCategoryService.deleteCategory(categoryId);
        return getSuccessResponseVO(null);
    }

    /**
     * 分类排序（批量调整）
     * 参数：List<{categoryId, sortOrder}>
     */
    @PostMapping("/category/sort")
    @GlobalInterceptor(checkAdmin = true)
    public ResponseVO<Void> sortCategory(@RequestBody List<VideoCategory> categories) {
        videoCategoryService.batchUpdateSort(categories);
        return getSuccessResponseVO(null);
    }

    // ==================== 文件管理 ====================

    /**
     * 上传文件（管理员）
     */
    @PostMapping("/file/upload")
    @GlobalInterceptor(checkAdmin = true)
    public ResponseVO<Map<String, String>> uploadFile(@RequestParam("file") MultipartFile file,
                                                      @RequestParam("userId") String userId) {
        String fileId = fileResourceService.uploadFile(file, userId);
        Map<String, String> result = new HashMap<>();
        result.put("fileId", fileId);
        return getSuccessResponseVO(result);
    }

    /**
     * 获取文件信息
     */
    @GetMapping("/file/info/{fileId}")
    @GlobalInterceptor(checkAdmin = true)
    public ResponseVO<FileResource> getFileInfo(@PathVariable String fileId) {
        FileResource fileInfo = fileResourceService.getFileInfo(fileId);
        return getSuccessResponseVO(fileInfo);
    }

    /**
     * 下载文件（返回文件流）
     */
    @GetMapping("/file/download/{fileId}")
    @GlobalInterceptor(checkAdmin = true)
    public void downloadFile(@PathVariable String fileId, HttpServletResponse response) {
        fileResourceService.downloadFile(fileId, response);
    }

    /**
     * 获取TS文件（视频流）
     */
    @GetMapping("/file/ts/{fileId}")
    @GlobalInterceptor(checkAdmin = true)
    public void getTsFile(@PathVariable String fileId, HttpServletResponse response) {
        // 复用文件下载逻辑，或者根据实际需要返回TS流
        fileResourceService.downloadFile(fileId, response);
    }
    // ==================== 视频管理 ====================
    @PostMapping("/video/list")
    @GlobalInterceptor(checkAdmin = true)
    public ResponseVO<List<Video>> videoList(@RequestParam(defaultValue = "1") Integer pageNo,
                                             @RequestParam(defaultValue = "10") Integer pageSize,
                                             @RequestParam(required = false) Integer status,
                                             @RequestParam(required = false) String title) {
        return getSuccessResponseVO(videoService.getVideoListForAdmin(pageNo, pageSize, status, title));
    }

    @PostMapping("/video/audit")
    @GlobalInterceptor(checkAdmin = true)
    public ResponseVO<Void> auditVideo(@RequestParam String videoId,
                                       @RequestParam Integer status,
                                       @RequestParam(required = false) String reason) {
        videoService.auditVideo(videoId, status, reason);
        return getSuccessResponseVO(null);
    }

    @PostMapping("/video/delete")
    @GlobalInterceptor(checkAdmin = true)
    public ResponseVO<Void> deleteVideo(@RequestParam String videoId) {
        videoService.deleteVideoByAdmin(videoId);
        return getSuccessResponseVO(null);
    }

    @PostMapping("/video/recommend")
    @GlobalInterceptor(checkAdmin = true)
    public ResponseVO<Void> recommendVideo(@RequestParam String videoId,
                                           @RequestParam Integer recommendFlag) {
        videoService.recommendVideo(videoId, recommendFlag);
        return getSuccessResponseVO(null);
    }

    @PostMapping("/video/episodes")
    @GlobalInterceptor(checkAdmin = true)
    public ResponseVO<List<VideoEpisode>> getVideoEpisodes(@RequestParam String videoId) {
        return getSuccessResponseVO(videoService.getEpisodesByVideoId(videoId));
    }

    // ==================== 弹幕管理 ====================
    @PostMapping("/danmu/list")
    @GlobalInterceptor(checkAdmin = true)
    public ResponseVO<List<Danmu>> danmuList(@RequestParam(required = false) String videoId,
                                             @RequestParam(defaultValue = "1") Integer pageNo,
                                             @RequestParam(defaultValue = "10") Integer pageSize) {
        return getSuccessResponseVO(danmuService.getDanmuListForAdmin(videoId, pageNo, pageSize));
    }

    @PostMapping("/danmu/delete")
    @GlobalInterceptor(checkAdmin = true)
    public ResponseVO<Void> deleteDanmu(@RequestParam Long danmuId) {
        danmuService.deleteDanmuByAdmin(danmuId);
        return getSuccessResponseVO(null);
    }

    // ==================== 评论管理 ====================
    @PostMapping("/comment/list")
    @GlobalInterceptor(checkAdmin = true)
    public ResponseVO<List<Comment>> commentList(@RequestParam(required = false) String videoId,
                                                 @RequestParam(defaultValue = "1") Integer pageNo,
                                                 @RequestParam(defaultValue = "10") Integer pageSize) {
        return getSuccessResponseVO(commentService.getCommentListForAdmin(videoId, pageNo, pageSize));
    }

    @PostMapping("/comment/delete")
    @GlobalInterceptor(checkAdmin = true)
    public ResponseVO<Void> deleteComment(@RequestParam Long commentId) {
        commentService.deleteCommentByAdmin(commentId);
        return getSuccessResponseVO(null);
    }

    // ==================== 用户管理 ====================
    @PostMapping("/user/list")
    @GlobalInterceptor(checkAdmin = true)
    public ResponseVO<List<UserInfo>> userList(@RequestParam(defaultValue = "1") Integer pageNo,
                                               @RequestParam(defaultValue = "10") Integer pageSize,
                                               @RequestParam(required = false) String nickName,
                                               @RequestParam(required = false) Integer status) {
        return getSuccessResponseVO(userInfoService.getUserListForAdmin(pageNo, pageSize, nickName, status));
    }

    @PostMapping("/user/changeStatus")
    @GlobalInterceptor(checkAdmin = true)
    public ResponseVO<Void> changeUserStatus(@RequestParam String userId,
                                             @RequestParam Integer status) {
        userInfoService.changeUserStatus(userId, status);
        return getSuccessResponseVO(null);
    }

    // ==================== 统计信息 ====================
    @PostMapping("/index/getActualTimeStatisticsInfo")
    @GlobalInterceptor(checkAdmin = true)
    public ResponseVO<Map<String, Object>> getActualTimeStatisticsInfo() {
        return getSuccessResponseVO(sysConfigService.getActualTimeStatistics());
    }

    @PostMapping("/index/getWeekStatisticsInfo")
    @GlobalInterceptor(checkAdmin = true)
    public ResponseVO<List<Map<String, Object>>> getWeekStatisticsInfo() {
        return getSuccessResponseVO(sysConfigService.getWeekStatistics());
    }

    // ==================== 系统设置 ====================
    @PostMapping("/saveListSettings")
    @GlobalInterceptor(checkAdmin = true)
    public ResponseVO<Void> saveListSettings(@RequestParam String module,
                                             @RequestBody Map<String, Object> settings) {
        sysConfigService.saveListSettings(module, settings);
        return getSuccessResponseVO(null);
    }
}