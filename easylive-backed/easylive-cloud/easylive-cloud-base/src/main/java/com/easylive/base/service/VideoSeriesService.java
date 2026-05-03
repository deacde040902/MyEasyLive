package com.easylive.base.service;

import com.easylive.base.entity.po.Video;
import com.easylive.base.entity.po.VideoSeries;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 视频系列表 服务类
 * </p>
 *
 * @author licheng
 * @since 2026-04-21
 */
public interface VideoSeriesService extends IService<VideoSeries> {
    VideoSeries createSeries(String userId, String seriesName);
    void updateSeries(Long seriesId, String userId, String seriesName, Integer sortOrder);
    void deleteSeries(Long seriesId, String userId);
    List<VideoSeries> getSeriesList(String userId);
    void changeSort(Long seriesId, String userId, Integer newSort);
    List<Video> getSeriesVideos(Long seriesId, Integer pageNo, Integer pageSize);
    void saveSeriesVideo(String userId, Long seriesId, String videoId);
    void delSeriesVideo(String userId, Long seriesId, String videoId);
    /**
     * 获取系列详情
     */
    VideoSeries getSeriesDetail(Long seriesId);

    /**
     * 删除系列（与 deleteSeries 同义，保留两种命名）
     */
    void delSeries(Long seriesId, String userId);

    /**
     * 保存系列（新增或修改）
     */
    void saveSeries(String userId, Long seriesId, String seriesName, Integer sortOrder);

    /**
     * 获取用户的所有系列，并附带每个系列下的视频列表
     */
    List<VideoSeries> getSeriesWithVideos(String userId);
}
