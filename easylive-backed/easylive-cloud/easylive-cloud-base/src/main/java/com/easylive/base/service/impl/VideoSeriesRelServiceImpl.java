package com.easylive.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.easylive.base.entity.po.Video;
import com.easylive.base.entity.po.VideoSeriesRel;
import com.easylive.base.exception.BusinessException;
import com.easylive.base.mapper.VideoSeriesRelMapper;
import com.easylive.base.service.VideoSeriesRelService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easylive.base.service.VideoService;
import com.easylive.base.service.impl.VideoSeriesCommonService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * <p>
 * 视频系列关联表 服务实现类
 * </p>
 *
 * @author licheng
 * @since 2026-04-21
 */
@Service
@Lazy
public class VideoSeriesRelServiceImpl extends ServiceImpl<VideoSeriesRelMapper, VideoSeriesRel> implements VideoSeriesRelService {

    @Resource
    private VideoService videoService;
    @Resource
    private VideoSeriesCommonService videoSeriesCommonService;

    @Override
    @Transactional
    public void addVideoToSeries(String videoId, Long seriesId, String userId) {
        Video video = videoService.getById(videoId);
        if (video == null || !video.getUserId().equals(userId)) {
            throw new BusinessException("视频不存在或无权限");
        }
        // 验证系列是否存在且用户有权限
        videoSeriesCommonService.validateSeries(seriesId, userId);
        // 检查视频是否已在系列中
        if (videoSeriesCommonService.isVideoInSeries(videoId, seriesId)) {
            throw new BusinessException("视频已在该系列中");
        }
        // 添加视频到系列
        videoSeriesCommonService.addVideoToSeries(videoId, seriesId);
    }

    @Override
    @Transactional
    public void removeVideoFromSeries(String videoId, Long seriesId, String userId) {
        // 验证系列是否存在且用户有权限
        videoSeriesCommonService.validateSeries(seriesId, userId);
        // 从系列中移除视频
        videoSeriesCommonService.removeVideoFromSeries(videoId, seriesId);
    }
}
