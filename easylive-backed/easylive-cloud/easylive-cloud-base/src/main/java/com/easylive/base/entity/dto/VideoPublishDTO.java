package com.easylive.base.entity.dto;

import lombok.Data;
import java.util.List;

@Data
public class VideoPublishDTO {
    private String title;           // 视频标题
    private String description;     // 视频描述
    private Integer categoryId;     // 分类ID
    private String coverFileId;     // 封面图片文件ID（已上传）
    private List<EpisodeDTO> episodes; // 分P列表
    private Integer isBigVideo;

    @Data
    public static class EpisodeDTO {
        private String title;       // 分P标题
        private Integer episodeNum; // 分P序号
        private String fileId;      // 视频文件ID（已上传）
        private Integer duration;   // 本P时长（秒）
        private String m3u8Url;     // 可选，转码后更新
    }

    public Integer getIsBigVideo() {
        return isBigVideo;
    }

    public void setIsBigVideo(Integer isBigVideo) {
        this.isBigVideo = isBigVideo;
    }
}