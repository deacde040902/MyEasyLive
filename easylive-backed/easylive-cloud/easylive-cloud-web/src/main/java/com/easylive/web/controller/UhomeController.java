package com.easylive.web.controller;

import com.easylive.base.annotation.GlobalInterceptor;
import com.easylive.base.controller.ABaseController;
import com.easylive.base.entity.po.VideoSeries;
import com.easylive.base.entity.vo.ResponseVO;
import com.easylive.base.exception.BusinessException;
import com.easylive.base.service.*;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/uhome")
public class UhomeController extends ABaseController {

    @Resource
    private UserInfoService userInfoService;
    @Resource
    private VideoService videoService;
    @Resource
    private UserFollowService userFollowService;
    @Resource
    private VideoSeriesService videoSeriesService;

    // 系列详情
    @PostMapping("/series/getVideoSeriesDetail")
    public ResponseVO<VideoSeries> getVideoSeriesDetail(@RequestParam Long seriesId) {
        VideoSeries series = videoSeriesService.getSeriesDetail(seriesId);
        return getSuccessResponseVO(series);
    }

    // 删除系列
    @PostMapping("/series/delVideoSeries")
    @GlobalInterceptor
    public ResponseVO<Void> delVideoSeries(@RequestParam Long seriesId, HttpServletRequest request) {
        String userId = getUserIdByToken(request);
        if (userId == null) throw new BusinessException("请先登录");
        videoSeriesService.delSeries(seriesId, userId);
        return getSuccessResponseVO(null);
    }

    // 保存系列（新增/修改）
    @PostMapping("/series/saveVideoSeries")
    @GlobalInterceptor
    public ResponseVO<Void> saveVideoSeries(@RequestParam(required = false) Long seriesId,
                                            @RequestParam String seriesName,
                                            @RequestParam(required = false) Integer sortOrder,
                                            HttpServletRequest request) {
        String userId = getUserIdByToken(request);
        if (userId == null) throw new BusinessException("请先登录");
        videoSeriesService.saveSeries(userId, seriesId, seriesName, sortOrder);
        return getSuccessResponseVO(null);
    }

    // 保存系列视频（添加视频到系列）
    @PostMapping("/series/saveSeriesVideo")
    @GlobalInterceptor
    public ResponseVO<Void> saveSeriesVideo(@RequestParam Long seriesId,
                                            @RequestParam String videoId,
                                            HttpServletRequest request) {
        String userId = getUserIdByToken(request);
        if (userId == null) throw new BusinessException("请先登录");
        videoSeriesService.saveSeriesVideo(userId, seriesId, videoId);
        return getSuccessResponseVO(null);
    }

    // 删除系列视频
    @PostMapping("/series/delSeriesVideo")
    @GlobalInterceptor
    public ResponseVO<Void> delSeriesVideo(@RequestParam Long seriesId,
                                           @RequestParam String videoId,
                                           HttpServletRequest request) {
        String userId = getUserIdByToken(request);
        if (userId == null) throw new BusinessException("请先登录");
        videoSeriesService.delSeriesVideo(userId, seriesId, videoId);
        return getSuccessResponseVO(null);
    }

    // 获取所有系列及其视频
    @PostMapping("/series/loadVideoSeriesWithVideo")
    public ResponseVO<List<VideoSeries>> loadVideoSeriesWithVideo(@RequestParam String userId) {
        List<VideoSeries> list = videoSeriesService.getSeriesWithVideos(userId);
        return getSuccessResponseVO(list);
    }
}