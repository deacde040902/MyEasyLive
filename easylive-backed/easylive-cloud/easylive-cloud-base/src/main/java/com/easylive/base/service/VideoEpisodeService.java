package com.easylive.base.service;

import com.easylive.base.entity.po.VideoEpisode;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 视频分P表 服务类
 * </p>
 *
 * @author licheng
 * @since 2026-04-19
 */
public interface VideoEpisodeService extends IService<VideoEpisode> {
    List<VideoEpisode> getEpisodesByVideoId(String videoId);
}
