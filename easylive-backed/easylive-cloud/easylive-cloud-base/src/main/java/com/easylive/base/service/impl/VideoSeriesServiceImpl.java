package com.easylive.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.easylive.base.entity.po.Video;
import com.easylive.base.entity.po.VideoSeries;
import com.easylive.base.entity.po.VideoSeriesRel;
import com.easylive.base.exception.BusinessException;
import com.easylive.base.mapper.VideoSeriesMapper;
import com.easylive.base.service.VideoSeriesService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easylive.base.service.VideoService;
import com.easylive.base.service.impl.VideoSeriesCommonService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 视频系列表 服务实现类
 * </p>
 *
 * @author licheng
 * @since 2026-04-21
 */
@Service
@Lazy
public class VideoSeriesServiceImpl extends ServiceImpl<VideoSeriesMapper, VideoSeries> implements VideoSeriesService {

    @Resource
    private VideoService videoService;
    @Resource
    private VideoSeriesCommonService videoSeriesCommonService;

    @Override
    @Transactional
    public VideoSeries createSeries(String userId, String seriesName) {
        if (!StringUtils.hasText(seriesName)) {
            throw new BusinessException("系列名称不能为空");
        }
        VideoSeries series = new VideoSeries();
        series.setUserId(userId);
        series.setSeriesName(seriesName);
        series.setSortOrder(0);
        series.setCreateTime(LocalDateTime.now());
        this.save(series);
        return series;
    }

    @Override
    @Transactional
    public void updateSeries(Long seriesId, String userId, String seriesName, Integer sortOrder) {
        VideoSeries series = this.getById(seriesId);
        if (series == null || !series.getUserId().equals(userId)) {
            throw new BusinessException("系列不存在或无权限");
        }
        if (StringUtils.hasText(seriesName)) {
            series.setSeriesName(seriesName);
        }
        if (sortOrder != null) {
            series.setSortOrder(sortOrder);
        }
        series.setUpdateTime(LocalDateTime.now());
        this.updateById(series);
    }

    @Override
    @Transactional
    public void deleteSeries(Long seriesId, String userId) {
        VideoSeries series = this.getById(seriesId);
        if (series == null || !series.getUserId().equals(userId)) {
            throw new BusinessException("系列不存在或无权限");
        }
        // 删除关联关系
        videoSeriesCommonService.deleteSeriesRelations(seriesId);
        this.removeById(seriesId);
    }

    @Override
    public List<VideoSeries> getSeriesList(String userId) {
        return this.list(new LambdaQueryWrapper<VideoSeries>()
                .eq(VideoSeries::getUserId, userId)
                .orderByAsc(VideoSeries::getSortOrder)
                .orderByAsc(VideoSeries::getCreateTime));
    }

    @Override
    @Transactional
    public void changeSort(Long seriesId, String userId, Integer newSort) {
        VideoSeries series = this.getById(seriesId);
        if (series == null || !series.getUserId().equals(userId)) {
            throw new BusinessException("系列不存在或无权限");
        }
        series.setSortOrder(newSort);
        series.setUpdateTime(LocalDateTime.now());
        this.updateById(series);
    }

    @Override
    public List<Video> getSeriesVideos(Long seriesId, Integer pageNo, Integer pageSize) {
        List<VideoSeriesRel> rels = videoSeriesCommonService.getSeriesVideoRels(seriesId);
        if (rels.isEmpty()) {
            return Collections.emptyList();
        }
        List<String> videoIds = rels.stream().map(VideoSeriesRel::getVideoId).collect(Collectors.toList());
        Page<Video> page = new Page<>(pageNo, pageSize);
        LambdaQueryWrapper<Video> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Video::getVideoId, videoIds).orderByDesc(Video::getCreateTime);
        return videoService.page(page, wrapper).getRecords();
    }

    @Override
    public VideoSeries getSeriesDetail(Long seriesId) {
        VideoSeries series = this.getById(seriesId);
        if (series == null) throw new BusinessException("系列不存在");
        return series;
    }

    @Override
    @Transactional
    public void saveSeries(String userId, Long seriesId, String seriesName, Integer sortOrder) {
        if (seriesId == null || seriesId <= 0) {
            // 新增
            VideoSeries series = new VideoSeries();
            series.setUserId(userId);
            series.setSeriesName(seriesName);
            series.setSortOrder(sortOrder != null ? sortOrder : 0);
            series.setCreateTime(LocalDateTime.now());
            this.save(series);
        } else {
            // 修改
            VideoSeries series = this.getById(seriesId);
            if (series == null || !series.getUserId().equals(userId)) {
                throw new BusinessException("系列不存在或无权限");
            }
            if (StringUtils.hasText(seriesName)) series.setSeriesName(seriesName);
            if (sortOrder != null) series.setSortOrder(sortOrder);
            series.setUpdateTime(LocalDateTime.now());
            this.updateById(series);
        }
    }

    @Override
    @Transactional
    public void delSeries(Long seriesId, String userId) {
        VideoSeries series = this.getById(seriesId);
        if (series == null || !series.getUserId().equals(userId)) {
            throw new BusinessException("系列不存在或无权限");
        }
        // 删除关联的视频关系
        videoSeriesCommonService.deleteSeriesRelations(seriesId);
        this.removeById(seriesId);
    }

    @Override
    public List<VideoSeries> getSeriesWithVideos(String userId) {
        List<VideoSeries> seriesList = getSeriesList(userId);
        for (VideoSeries series : seriesList) {
            List<Video> videos = getSeriesVideos(series.getSeriesId(), 1, 100); // 获取前100个视频
            series.setVideos(videos);
        }
        return seriesList;
    }

    @Override
    @Transactional
    public void saveSeriesVideo(String userId, Long seriesId, String videoId) {
        Video video = videoService.getById(videoId);
        if (video == null || !video.getUserId().equals(userId)) {
            throw new BusinessException("视频不存在或无权限");
        }
        VideoSeries series = this.getById(seriesId);
        if (series == null || !series.getUserId().equals(userId)) {
            throw new BusinessException("系列不存在或无权限");
        }
        // 检查是否已存在
        if (!videoSeriesCommonService.isVideoInSeries(videoId, seriesId)) {
            videoSeriesCommonService.addVideoToSeries(videoId, seriesId);
        }
    }

    @Override
    @Transactional
    public void delSeriesVideo(String userId, Long seriesId, String videoId) {
        VideoSeries series = this.getById(seriesId);
        if (series == null || !series.getUserId().equals(userId)) {
            throw new BusinessException("系列不存在或无权限");
        }
        // 删除视频从系列中
        videoSeriesCommonService.removeVideoFromSeries(videoId, seriesId);
    }
}
