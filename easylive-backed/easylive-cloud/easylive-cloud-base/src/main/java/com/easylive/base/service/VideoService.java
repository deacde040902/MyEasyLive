package com.easylive.base.service;

import com.easylive.base.entity.dto.VideoPublishDTO;
import com.easylive.base.entity.po.Video;
import com.baomidou.mybatisplus.extension.service.IService;
import com.easylive.base.entity.po.VideoEpisode;

import java.util.List;

/**
 * <p>
 * 视频主表 服务类
 * </p>
 *
 * @author licheng
 * @since 2026-04-19
 */
public interface VideoService extends IService<Video> {
    List<Video> getVideoListByUserId(String userId, Integer pageNo, Integer pageSize);
    long countByUserId(String userId);
    Video getVideoDetail(String videoId, String userId);
    void deleteVideo(String videoId, String userId);
    List<Video> getVideoList(Integer pageNo, Integer pageSize, Integer categoryId, Integer tagId, String orderBy);
    Video getVideoDetailForUser(String videoId, String userId);
    void incrementPlayCount(String videoId);
    String publishVideo(VideoPublishDTO videoDTO, String userId);
    List<Video> getRecommendVideos(Integer pageNo, Integer pageSize);
    List<Video> getHotVideos(Integer pageNo, Integer pageSize);
    List<Video> getVideoListForAdmin(Integer pageNo, Integer pageSize, Integer status, String title);
    void auditVideo(String videoId, Integer status, String reason);
    void deleteVideoByAdmin(String videoId);
    void recommendVideo(String videoId, Integer recommendFlag);
    List<VideoEpisode> getEpisodesByVideoId(String videoId);
    void updateVideo(String videoId, Video video, String userId);
    List<Video> getBigVideos();

}
