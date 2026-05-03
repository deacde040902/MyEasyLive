package com.easylive.web.controller;

import com.easylive.base.annotation.GlobalInterceptor;
import com.easylive.base.controller.ABaseController;

import com.easylive.base.entity.po.Video;
import com.easylive.base.entity.vo.ResponseVO;
import com.easylive.base.exception.BusinessException;
import com.easylive.base.redis.RedisComponent;
import com.easylive.base.service.VideoService;
import com.easylive.base.service.UserCreationSettingsService;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import com.easylive.base.entity.dto.VideoPublishDTO;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/creation")
public class CreationCenterController extends ABaseController {

    @Resource
    private VideoService videoService;
    @Resource
    private UserCreationSettingsService userCreationSettingsService;

    /**
     * 创作中心视频列表
     */
    @PostMapping("/videoList")
    @GlobalInterceptor
    public ResponseVO<List<Video>> videoList(HttpServletRequest request,
                                             @RequestParam(defaultValue = "1") Integer pageNo,
                                             @RequestParam(defaultValue = "10") Integer pageSize) {
        String userId = getUserIdByToken(request);
        if (userId == null) throw new BusinessException("登录已过期");
        List<Video> list = videoService.getVideoListByUserId(userId, pageNo, pageSize);
        return getSuccessResponseVO(list);
    }

    /**
     * 创作中心视频数量
     */
    @PostMapping("/videoCount")
    @GlobalInterceptor
    public ResponseVO<Long> videoCount(HttpServletRequest request) {
        String userId = getUserIdByToken(request);
        if (userId == null) throw new BusinessException("登录已过期");
        long count = videoService.countByUserId(userId);
        return getSuccessResponseVO(count);
    }

    /**
     * 获取视频详情
     */
    @PostMapping("/getVideoDetail")
    @GlobalInterceptor
    public ResponseVO<Video> getVideoDetail(@RequestParam String videoId,
                                            HttpServletRequest request) {
        String userId = getUserIdByToken(request);
        if (userId == null) throw new BusinessException("登录已过期");
        Video video = videoService.getVideoDetail(videoId, userId);
        return getSuccessResponseVO(video);
    }

    /**
     * 保存创作中心设置
     */
    @PostMapping("/saveSettings")
    @GlobalInterceptor
    public ResponseVO<Void> saveSettings(@RequestBody Map<String, String> settings,
                                         HttpServletRequest request) {
        String userId = getUserIdByToken(request);
        if (userId == null) throw new BusinessException("登录已过期");
        userCreationSettingsService.saveSettings(userId, settings);
        return getSuccessResponseVO(null);
    }

    /**
     * 删除视频
     */
    @PostMapping("/deleteVideo")
    @GlobalInterceptor
    public ResponseVO<Void> deleteVideo(@RequestParam String videoId,
                                        HttpServletRequest request) {
        String userId = getUserIdByToken(request);
        if (userId == null) throw new BusinessException("登录已过期");
        videoService.deleteVideo(videoId, userId);
        return getSuccessResponseVO(null);
    }

    /**
     * 发布视频（含分P信息）
     * @param videoDTO 视频信息（包含标题、描述、分类、封面文件ID及分P列表）
     * @param request HTTP请求（用于获取token）
     * @return 视频ID
     */
    @PostMapping("/publish")
    @GlobalInterceptor
    public ResponseVO<String> publishVideo(@RequestBody VideoPublishDTO videoDTO,
                                           HttpServletRequest request) {
        String userId = getUserIdByToken(request);
        if (userId == null) throw new BusinessException("登录已过期");
        String videoId = videoService.publishVideo(videoDTO, userId);
        return getSuccessResponseVO(videoId);
    }
}