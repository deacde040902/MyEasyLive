package com.easylive.web.controller;

import com.easylive.base.annotation.GlobalInterceptor;
import com.easylive.base.controller.ABaseController;
import com.easylive.base.entity.po.VideoSeries;
import com.easylive.base.entity.vo.ResponseVO;
import com.easylive.base.exception.BusinessException;
import com.easylive.base.service.VideoSeriesService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 * 视频系列表 前端控制器
 * </p>
 *
 * @author licheng
 * @since 2026-04-21
 */
@RestController
@RequestMapping("/videoSeries")
public class VideoSeriesController extends ABaseController {

    @Resource
    private VideoSeriesService videoSeriesService;

    @PostMapping("/create")
    @GlobalInterceptor
    public ResponseVO<VideoSeries> createSeries(@RequestParam String seriesName, HttpServletRequest request) {
        String userId = getUserIdByToken(request);
        if (userId == null) throw new BusinessException("请先登录");
        VideoSeries series = videoSeriesService.createSeries(userId, seriesName);
        return getSuccessResponseVO(series);
    }

    @PostMapping("/update")
    @GlobalInterceptor
    public ResponseVO<Void> updateSeries(@RequestParam Long seriesId,
                                         @RequestParam(required = false) String seriesName,
                                         @RequestParam(required = false) Integer sortOrder,
                                         HttpServletRequest request) {
        String userId = getUserIdByToken(request);
        if (userId == null) throw new BusinessException("请先登录");
        videoSeriesService.updateSeries(seriesId, userId, seriesName, sortOrder);
        return getSuccessResponseVO(null);
    }

    @PostMapping("/delete")
    @GlobalInterceptor
    public ResponseVO<Void> deleteSeries(@RequestParam Long seriesId, HttpServletRequest request) {
        String userId = getUserIdByToken(request);
        if (userId == null) throw new BusinessException("请先登录");
        videoSeriesService.deleteSeries(seriesId, userId);
        return getSuccessResponseVO(null);
    }

    @PostMapping("/list")
    public ResponseVO<List<VideoSeries>> listSeries(@RequestParam String userId) {
        List<VideoSeries> list = videoSeriesService.getSeriesList(userId);
        return getSuccessResponseVO(list);
    }

    @PostMapping("/changeSort")
    @GlobalInterceptor
    public ResponseVO<Void> changeSort(@RequestParam Long seriesId, @RequestParam Integer newSort, HttpServletRequest request) {
        String userId = getUserIdByToken(request);
        if (userId == null) throw new BusinessException("请先登录");
        videoSeriesService.changeSort(seriesId, userId, newSort);
        return getSuccessResponseVO(null);
    }
}
