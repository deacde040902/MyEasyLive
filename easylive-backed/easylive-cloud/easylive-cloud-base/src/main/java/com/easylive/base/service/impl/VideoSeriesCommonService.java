package com.easylive.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.easylive.base.entity.po.VideoSeries;
import com.easylive.base.entity.po.VideoSeriesRel;
import com.easylive.base.exception.BusinessException;
import com.easylive.base.mapper.VideoSeriesMapper;
import com.easylive.base.mapper.VideoSeriesRelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * 视频系列公共服务类，用于分解循环依赖
 */
@Service
public class VideoSeriesCommonService {

    @Resource
    private VideoSeriesMapper videoSeriesMapper;
    @Resource
    private VideoSeriesRelMapper videoSeriesRelMapper;

    /**
     * 验证系列是否存在且用户有权限
     */
    public VideoSeries validateSeries(Long seriesId, String userId) {
        VideoSeries series = videoSeriesMapper.selectById(seriesId);
        if (series == null || !series.getUserId().equals(userId)) {
            throw new BusinessException("系列不存在或无权限");
        }
        return series;
    }

    /**
     * 删除系列的关联关系
     */
    @Transactional
    public void deleteSeriesRelations(Long seriesId) {
        videoSeriesRelMapper.delete(
                new LambdaQueryWrapper<VideoSeriesRel>().eq(VideoSeriesRel::getSeriesId, seriesId)
        );
    }

    /**
     * 检查视频是否已在系列中
     */
    public boolean isVideoInSeries(String videoId, Long seriesId) {
        VideoSeriesRel rel = videoSeriesRelMapper.selectOne(
                new LambdaQueryWrapper<VideoSeriesRel>()
                        .eq(VideoSeriesRel::getVideoId, videoId)
                        .eq(VideoSeriesRel::getSeriesId, seriesId)
        );
        return rel != null;
    }

    /**
     * 添加视频到系列
     */
    @Transactional
    public void addVideoToSeries(String videoId, Long seriesId) {
        VideoSeriesRel rel = new VideoSeriesRel();
        rel.setVideoId(videoId);
        rel.setSeriesId(seriesId);
        rel.setSortOrder(0);
        videoSeriesRelMapper.insert(rel);
    }

    /**
     * 从系列中移除视频
     */
    @Transactional
    public void removeVideoFromSeries(String videoId, Long seriesId) {
        videoSeriesRelMapper.delete(
                new LambdaQueryWrapper<VideoSeriesRel>()
                        .eq(VideoSeriesRel::getVideoId, videoId)
                        .eq(VideoSeriesRel::getSeriesId, seriesId)
        );
    }

    /**
     * 获取系列的视频关联列表
     */
    public java.util.List<VideoSeriesRel> getSeriesVideoRels(Long seriesId) {
        return videoSeriesRelMapper.selectList(
                new LambdaQueryWrapper<VideoSeriesRel>()
                        .eq(VideoSeriesRel::getSeriesId, seriesId)
                        .orderByAsc(VideoSeriesRel::getSortOrder)
        );
    }
}