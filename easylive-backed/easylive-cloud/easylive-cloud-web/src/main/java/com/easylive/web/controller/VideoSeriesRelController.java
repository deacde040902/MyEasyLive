package com.easylive.web.controller;

import com.easylive.base.annotation.GlobalInterceptor;
import com.easylive.base.controller.ABaseController;
import com.easylive.base.entity.vo.ResponseVO;
import com.easylive.base.exception.BusinessException;
import com.easylive.base.redis.RedisComponent;
import com.easylive.base.service.VideoSeriesRelService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 视频系列关联表 前端控制器
 */
@RestController
@RequestMapping("/videoSeriesRel")
public class VideoSeriesRelController extends ABaseController {

    @Resource
    private VideoSeriesRelService videoSeriesRelService;
    @Resource
    private RedisComponent redisComponent;

    /**
     * 将视频添加到系列（需登录）
     */
    @PostMapping("/add")
    @GlobalInterceptor
    public ResponseVO<Void> addVideoToSeries(@RequestParam String videoId,
                                             @RequestParam Long seriesId,
                                             HttpServletRequest request) {
        String userId = getUserIdByToken(request);
        if (userId == null) {
            throw new BusinessException("请先登录");
        }
        videoSeriesRelService.addVideoToSeries(videoId, seriesId, userId);
        return getSuccessResponseVO(null);
    }

    /**
     * 从系列中移除视频（需登录）
     */
    @PostMapping("/remove")
    @GlobalInterceptor
    public ResponseVO<Void> removeVideoFromSeries(@RequestParam String videoId,
                                                  @RequestParam Long seriesId,
                                                  HttpServletRequest request) {
        String userId = getUserIdByToken(request);
        if (userId == null) {
            throw new BusinessException("请先登录");
        }
        videoSeriesRelService.removeVideoFromSeries(videoId, seriesId, userId);
        return getSuccessResponseVO(null);
    }
}