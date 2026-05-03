package com.easylive.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.easylive.base.entity.po.VideoEpisode;
import com.easylive.base.mapper.VideoEpisodeMapper;
import com.easylive.base.service.VideoEpisodeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 视频分P表 服务实现类
 * </p>
 *
 * @author licheng
 * @since 2026-04-19
 */
@Service
public class VideoEpisodeServiceImpl extends ServiceImpl<VideoEpisodeMapper, VideoEpisode> implements VideoEpisodeService {
    @Override
    public List<VideoEpisode> getEpisodesByVideoId(String videoId) {
        LambdaQueryWrapper<VideoEpisode> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(VideoEpisode::getVideoId, videoId)
                .orderByAsc(VideoEpisode::getEpisodeNum);
        return this.list(wrapper);
    }
}
