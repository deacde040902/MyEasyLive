package com.easylive.base.service.impl;

import com.easylive.base.entity.po.VideoEpisode;
import com.easylive.base.service.VideoEpisodeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class UpdateM3u8UrlTest {

    @Autowired
    private VideoEpisodeService videoEpisodeService;

    @Value("${gateway.host:http://localhost:7071}")
    private String gatewayHost;

    @Test
    public void updateM3u8Url() {
        // 视频ID
        String videoId = "2048050206913552386";
        // 分P ID
        String episodeId = "2048050206913552387";
        
        // 获取分P信息
        VideoEpisode episode = videoEpisodeService.getById(episodeId);
        if (episode != null) {
            // 构建m3u8 URL
            String m3u8Url = gatewayHost + "/hls/" + videoId + "/" + episodeId + "/index.m3u8";
            // 更新m3u8 URL
            episode.setM3u8Url(m3u8Url);
            boolean updated = videoEpisodeService.updateById(episode);
            System.out.println("更新m3u8Url结果: " + updated);
            System.out.println("更新后的m3u8Url: " + episode.getM3u8Url());
        } else {
            System.out.println("分P不存在，episodeId: " + episodeId);
        }
    }

    @Test
    public void updateAllM3u8Url() {
        // 视频ID
        String videoId = "2048050206913552386";
        
        // 获取该视频的所有分P
        List<VideoEpisode> episodes = videoEpisodeService.getEpisodesByVideoId(videoId);
        for (VideoEpisode episode : episodes) {
            // 构建m3u8 URL
            String m3u8Url = gatewayHost + "/hls/" + videoId + "/" + episode.getEpisodeId() + "/index.m3u8";
            // 更新m3u8 URL
            episode.setM3u8Url(m3u8Url);
            boolean updated = videoEpisodeService.updateById(episode);
            System.out.println("更新分P " + episode.getEpisodeId() + " 结果: " + updated);
            System.out.println("更新后的m3u8Url: " + episode.getM3u8Url());
        }
    }
}
