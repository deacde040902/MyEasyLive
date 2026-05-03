package com.easylive.base.listener;

import com.easylive.base.event.TranscodeEpisodeEvent;
import com.easylive.base.service.VideoTranscodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class TranscodeEventListener {

    private static final Logger log = LoggerFactory.getLogger(TranscodeEventListener.class);

    @Resource
    private VideoTranscodeService videoTranscodeService;

    @Async
    @EventListener
    public void handleTranscodeEpisodeEvent(TranscodeEpisodeEvent event) {
        String episodeId = event.getEpisodeId();
        log.info("接收到转码事件，episodeId: {}", episodeId);
        videoTranscodeService.transcodeEpisodeAsync(episodeId);
    }
}