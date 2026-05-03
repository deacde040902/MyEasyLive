package com.easylive.base.service;

public interface VideoTranscodeService {
    /**
     * 异步转码视频分P
     * @param episodeId 分P ID
     */
    void transcodeEpisodeAsync(String episodeId);
}