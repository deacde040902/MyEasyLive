package com.easylive.base.event;

import org.springframework.context.ApplicationEvent;

/**
 * 视频分P转码事件
 */
public class TranscodeEpisodeEvent extends ApplicationEvent {
    private final String episodeId;

    public TranscodeEpisodeEvent(Object source, String episodeId) {
        super(source);
        this.episodeId = episodeId;
    }

    public String getEpisodeId() {
        return episodeId;
    }
}