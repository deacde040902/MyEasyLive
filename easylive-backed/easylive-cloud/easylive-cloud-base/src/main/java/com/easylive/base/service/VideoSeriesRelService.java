package com.easylive.base.service;

import com.easylive.base.entity.po.VideoSeriesRel;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 视频系列关联表 服务类
 * </p>
 *
 * @author licheng
 * @since 2026-04-21
 */
public interface VideoSeriesRelService extends IService<VideoSeriesRel> {
    void addVideoToSeries(String videoId, Long seriesId, String userId);
    void removeVideoFromSeries(String videoId, Long seriesId, String userId);
}
