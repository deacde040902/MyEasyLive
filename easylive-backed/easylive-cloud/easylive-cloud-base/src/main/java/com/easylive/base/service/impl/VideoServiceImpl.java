package com.easylive.base.service.impl;

import cn.hutool.core.io.FileUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easylive.base.entity.dto.VideoPublishDTO;
import com.easylive.base.entity.po.*;
import com.easylive.base.event.TranscodeEpisodeEvent;
import com.easylive.base.exception.BusinessException;
import com.easylive.base.mapper.UserActionMapper;
import com.easylive.base.mapper.UserCollectionMapper;
import com.easylive.base.mapper.VideoCommentMapper;
import com.easylive.base.mapper.VideoMapper;
import com.easylive.base.service.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import com.easylive.base.entity.dto.VideoPublishDTO;
import javax.annotation.Resource;
import java.io.File;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Service
public class VideoServiceImpl extends ServiceImpl<VideoMapper, Video> implements VideoService {

    @Resource
    private VideoMapper videoMapper;

    @Resource
    private VideoEpisodeService videoEpisodeService;

    @Resource
    private ApplicationEventPublisher applicationEventPublisher;

    @Resource
    private UserInfoService userInfoService;

    @Resource
    private UserFollowService userFollowService;

    @Resource
    private FileResourceService fileResourceService;

    @Resource
    private UserActionMapper userActionMapper;

    @Resource
    private UserCollectionMapper userCollectionMapper;

    @Resource
    private VideoCommentMapper videoCommentMapper;

    @Value("${gateway.host:http://localhost:7071}")
    private String gatewayHost;

    @Value("${easylive.hls.output-path:${easylive.file.base-path}/hls}")
    private String hlsOutputBasePath;


    @Override
    public List<Video> getVideoListByUserId(String userId, Integer pageNo, Integer pageSize) {
        // 分页查询用户视频列表（按创建时间倒序）
        Page<Video> page = new Page<>(pageNo, pageSize);
        LambdaQueryWrapper<Video> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Video::getUserId, userId)
                .orderByDesc(Video::getCreateTime);
        Page<Video> resultPage = videoMapper.selectPage(page, wrapper);
        return resultPage.getRecords();
    }

    @Override
    public long countByUserId(String userId) {
        LambdaQueryWrapper<Video> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Video::getUserId, userId);
        return videoMapper.selectCount(wrapper);
    }

    @Override
    public Video getVideoDetail(String videoId, String userId) {
        Video video = this.getById(videoId);
        if (video == null) {
            throw new BusinessException("视频不存在");
        }
        // 可选：校验视频是否属于当前用户（如果要求只能查看自己的视频）
        if (!video.getUserId().equals(userId)) {
            throw new BusinessException("无权查看此视频");
        }
        // 可以在这里增加播放量等统计，或关联查询其他信息（如分P列表）
        return video;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteVideo(String videoId, String userId) {
        // 1. 查询视频是否存在
        Video video = this.getById(videoId);
        if (video == null) {
            throw new BusinessException("视频不存在");
        }
        // 2. 权限校验：只能删除自己的视频
        if (!video.getUserId().equals(userId)) {
            throw new BusinessException("无权删除此视频");
        }
        // 3. 物理删除（或逻辑删除，取决于表设计）
        // 这里使用物理删除，如果需要逻辑删除，可在表中增加 status 字段
        boolean removed = this.removeById(videoId);
        if (!removed) {
            throw new BusinessException("删除失败，请稍后重试");
        }
// 删除分P及 HLS 文件
        List<VideoEpisode> episodes = videoEpisodeService.getEpisodesByVideoId(videoId);
        for (VideoEpisode ep : episodes) {
            String hlsDir = hlsOutputBasePath + File.separator + ep.getVideoId() + File.separator + ep.getEpisodeId();
            FileUtil.del(hlsDir);
            videoEpisodeService.removeById(ep.getEpisodeId());
        }
        userActionMapper.delete(new LambdaQueryWrapper<UserAction>().eq(UserAction::getVideoId, videoId));
        userCollectionMapper.delete(new LambdaQueryWrapper<UserCollection>().eq(UserCollection::getVideoId, videoId));
        videoCommentMapper.delete(new LambdaQueryWrapper<VideoComment>().eq(VideoComment::getVideoId, videoId));
        this.removeById(videoId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String publishVideo(VideoPublishDTO videoDTO, String userId) {
        // 校验
        if (StringUtils.isEmpty(videoDTO.getTitle())) {
            throw new BusinessException("视频标题不能为空");
        }
        if (CollectionUtils.isEmpty(videoDTO.getEpisodes())) {
            throw new BusinessException("至少需要一个视频分P");
        }

        // 保存视频主信息
        Video video = new Video();
        video.setVideoId(IdWorker.getIdStr());
        video.setUserId(userId);
        video.setTitle(videoDTO.getTitle());
        video.setDescription(videoDTO.getDescription());
        video.setCategoryId(videoDTO.getCategoryId());
        video.setCoverFileId(videoDTO.getCoverFileId());
        video.setStatus((byte) 1);
        video.setIsBigVideo(videoDTO.getIsBigVideo());
        video.setPublishTime(LocalDateTime.now());
        video.setCreateTime(LocalDateTime.now());
        video.setUpdateTime(LocalDateTime.now());
        this.save(video);

        // 保存分P并触发异步转码
        List<VideoPublishDTO.EpisodeDTO> episodes = videoDTO.getEpisodes();
        for (int i = 0; i < episodes.size(); i++) {
            VideoPublishDTO.EpisodeDTO epDto = episodes.get(i);
            VideoEpisode episode = new VideoEpisode();
            episode.setEpisodeId(IdWorker.getIdStr());
            episode.setVideoId(video.getVideoId());
            episode.setTitle(epDto.getTitle());
            episode.setEpisodeNum(epDto.getEpisodeNum() != null ? epDto.getEpisodeNum() : i + 1);
            episode.setFileId(epDto.getFileId());
            episode.setDuration(epDto.getDuration() != null ? epDto.getDuration() : 0);
            episode.setM3u8Url(null);
            episode.setCreateTime(LocalDateTime.now());
            videoEpisodeService.save(episode);

            // 异步转码
            applicationEventPublisher.publishEvent(new TranscodeEpisodeEvent(this, episode.getEpisodeId()));
        }

        return video.getVideoId();
    }

    // 修改后的 getVideoList 方法（添加填充封面、作者信息和duration）
    @Override
    public List<Video> getVideoList(Integer pageNo, Integer pageSize, Integer categoryId, Integer tagId, String orderBy) {
        Page<Video> page = new Page<>(pageNo, pageSize);
        LambdaQueryWrapper<Video> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Video::getStatus, 1);

        if (categoryId != null) {
            wrapper.eq(Video::getCategoryId, categoryId);
        }

        if (tagId != null) {
            wrapper.inSql(Video::getVideoId,
                    "SELECT video_id FROM video_tag WHERE tag_id = " + tagId);
        }

        if ("play_count".equals(orderBy)) {
            wrapper.orderByDesc(Video::getPlayCount);
        } else {
            wrapper.orderByDesc(Video::getPublishTime);
        }

        Page<Video> result = baseMapper.selectPage(page, wrapper);

        // 填充作者信息、封面和duration
        for (Video video : result.getRecords()) {
            // 填充作者信息
            if (StringUtils.hasText(video.getUserId())) {
                UserInfo author = userInfoService.getById(video.getUserId());
                if (author != null) {
                    video.setAuthorName(author.getNickName());
                    // 如果 author.getAvatarPath() 就是文件ID，则拼接网关下载地址
                    String avatarFileId = author.getAvatarPath();
                    if (StringUtils.hasText(author.getAvatarPath())) {
                        video.setAuthorAvatar("/resource/" + author.getAvatarPath());
                    }
                }
            }

            // 填充封面URL
            if (StringUtils.hasText(video.getCoverFileId())) {
                video.setCover("/resource/public/download/" + video.getCoverFileId());
            }
            
            // 填充duration（从第一个分P获取，如果主表没有）
            if (video.getDuration() == null || video.getDuration() == 0) {
                List<VideoEpisode> episodes = videoEpisodeService.getEpisodesByVideoId(video.getVideoId());
                if (episodes != null && !episodes.isEmpty()) {
                    VideoEpisode firstEpisode = episodes.get(0);
                    if (firstEpisode.getDuration() != null && firstEpisode.getDuration() > 0) {
                        video.setDuration(firstEpisode.getDuration());
                    }
                }
            }
        }

        return result.getRecords();
    }

    // 修改后的 getVideoDetailForUser 方法
    @Override
    public Video getVideoDetailForUser(String videoId, String userId) {
        Video video = this.getById(videoId);
        if (video == null || video.getStatus() != 1) {
            throw new BusinessException("视频不存在或已下架");
        }

        // 填充作者信息
        if (StringUtils.hasText(video.getUserId())) {
            UserInfo author = userInfoService.getById(video.getUserId());
            if (author != null) {
                video.setAuthorName(author.getNickName());
                if (StringUtils.hasText(author.getAvatarPath())) {
                    video.setAuthorAvatar("/resource/" + author.getAvatarPath());
                }
            }
        }

        // 填充封面URL
        if (StringUtils.hasText(video.getCoverFileId())) {
            FileResource coverFile = fileResourceService.getById(video.getCoverFileId());
            if (coverFile != null) {
                // 使用公开访问的URL
                video.setCover(gatewayHost + "/resource/public/download/" + video.getCoverFileId());
            }
        }

        // 查询视频播放地址
        List<VideoEpisode> episodes = videoEpisodeService.getEpisodesByVideoId(videoId);
        if (episodes != null && !episodes.isEmpty()) {
            VideoEpisode firstEpisode = episodes.get(0);
            if (StringUtils.hasText(firstEpisode.getM3u8Url())) {
                video.setVideoUrl(firstEpisode.getM3u8Url());
            }
            if (video.getDuration() == null || video.getDuration() == 0) {
                video.setDuration(firstEpisode.getDuration());
            }
        }

        return video;
    }

    @Override
    public void incrementPlayCount(String videoId) {
        this.lambdaUpdate()
                .eq(Video::getVideoId, videoId)
                .setSql("play_count = play_count + 1")
                .update();
    }

    @Override
    public List<Video> getRecommendVideos(Integer pageNo, Integer pageSize) {
        // 简单实现：按播放量排序，可结合用户历史
        Page<Video> page = new Page<>(pageNo, pageSize);
        LambdaQueryWrapper<Video> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Video::getStatus, 1).orderByDesc(Video::getPlayCount);
        List<Video> videos = this.page(page, wrapper).getRecords();
        
        // 填充作者信息、封面和duration
        fillVideoInfo(videos);
        
        return videos;
    }

    @Override
    public List<Video> getHotVideos(Integer pageNo, Integer pageSize) {
        // 按播放量排序
        return getRecommendVideos(pageNo, pageSize);
    }
    
    /**
     * 填充视频列表的作者信息、封面和duration
     */
    private void fillVideoInfo(List<Video> videos) {
        for (Video video : videos) {
            // 填充作者信息
            if (StringUtils.hasText(video.getUserId())) {
                UserInfo author = userInfoService.getById(video.getUserId());
                if (author != null) {
                    video.setAuthorName(author.getNickName());
                    if (StringUtils.hasText(author.getAvatarPath())) {
                        video.setAuthorAvatar("/resource/" + author.getAvatarPath());
                    }
                }
            }

            // 填充封面URL
            if (StringUtils.hasText(video.getCoverFileId())) {
                video.setCover("/resource/public/download/" + video.getCoverFileId());
            }
            
            // 填充duration（从第一个分P获取，如果主表没有）
            if (video.getDuration() == null || video.getDuration() == 0) {
                List<VideoEpisode> episodes = videoEpisodeService.getEpisodesByVideoId(video.getVideoId());
                if (episodes != null && !episodes.isEmpty()) {
                    VideoEpisode firstEpisode = episodes.get(0);
                    if (firstEpisode.getDuration() != null && firstEpisode.getDuration() > 0) {
                        video.setDuration(firstEpisode.getDuration());
                    }
                }
            }
        }
    }

    @Override
    public List<Video> getVideoListForAdmin(Integer pageNo, Integer pageSize, Integer status, String title) {
        Page<Video> page = new Page<>(pageNo, pageSize);
        LambdaQueryWrapper<Video> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(Video::getStatus, status);
        }
        if (StringUtils.hasText(title)) {
            wrapper.like(Video::getTitle, title);
        }
        wrapper.orderByDesc(Video::getCreateTime);
        return this.page(page, wrapper).getRecords();
    }

    @Override
    @Transactional
    public void auditVideo(String videoId, Integer status, String reason) {
        Video video = this.getById(videoId);
        if (video == null) {
            throw new BusinessException("视频不存在");
        }
        video.setStatus(status.byteValue()); // 注意 status 字段类型，如果是 Byte 需转换
        // 如果 Video 有 audit_reason 字段，可设置
        // video.setAuditReason(reason);
        this.updateById(video);
    }

    @Override
    @Transactional
    public void deleteVideoByAdmin(String videoId) {
        Video video = this.getById(videoId);
        if (video == null) {
            throw new BusinessException("视频不存在");
        }
        // 物理删除（或逻辑删除，根据业务）
        this.removeById(videoId);
        // 同时删除关联的分P、弹幕、评论等（可选）
    }

    @Override
    @Transactional
    public void recommendVideo(String videoId, Integer recommendFlag) {
        Video video = this.getById(videoId);
        if (video == null) {
            throw new BusinessException("视频不存在");
        }
        // 假设 video 表有 recommend_flag 字段
        // video.setRecommendFlag(recommendFlag);
        this.updateById(video);
    }

    @Override
    public List<VideoEpisode> getEpisodesByVideoId(String videoId) {
        return videoEpisodeService.getEpisodesByVideoId(videoId);
    }

    @Override
    @Transactional
    public void updateVideo(String videoId, Video video, String userId) {
        // 1. 查询视频是否存在
        Video existingVideo = this.getById(videoId);
        if (existingVideo == null) {
            throw new BusinessException("视频不存在");
        }
        // 2. 权限校验：只能更新自己的视频
        if (!existingVideo.getUserId().equals(userId)) {
            throw new BusinessException("无权更新此视频");
        }
        // 3. 更新视频信息（只更新非空字段）
        if (StringUtils.hasText(video.getTitle())) {
            existingVideo.setTitle(video.getTitle());
        }
        if (StringUtils.hasText(video.getDescription())) {
            existingVideo.setDescription(video.getDescription());
        }
        if (video.getCategoryId() != null) {
            existingVideo.setCategoryId(video.getCategoryId());
        }
        if (video.getIsBigVideo() != null) {
            existingVideo.setIsBigVideo(video.getIsBigVideo());
        }
        if (StringUtils.hasText(video.getCoverFileId())) {
            existingVideo.setCoverFileId(video.getCoverFileId());
        }
        existingVideo.setUpdateTime(LocalDateTime.now());
        this.updateById(existingVideo);
    }

    @Override
    public List<Video> getBigVideos() {
        LambdaQueryWrapper<Video> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Video::getStatus, 1);
        wrapper.eq(Video::getIsBigVideo, 1);
        wrapper.orderByDesc(Video::getPublishTime);
        wrapper.last("LIMIT 3");

        List<Video> videos = baseMapper.selectList(wrapper);

        // 填充作者信息、封面和duration
        for (Video video : videos) {
            // 填充作者信息
            if (StringUtils.hasText(video.getUserId())) {
                UserInfo author = userInfoService.getById(video.getUserId());
                if (author != null) {
                    video.setAuthorName(author.getNickName());
                    if (StringUtils.hasText(author.getAvatarPath())) {
                        video.setAuthorAvatar("/resource/" + author.getAvatarPath());
                    }
                }
            }

            // 填充封面URL
            if (StringUtils.hasText(video.getCoverFileId())) {
                video.setCover(gatewayHost + "/resource/public/download/" + video.getCoverFileId());
            }
            
            // 填充duration（从第一个分P获取，如果主表没有）
            if (video.getDuration() == null || video.getDuration() == 0) {
                List<VideoEpisode> episodes = videoEpisodeService.getEpisodesByVideoId(video.getVideoId());
                if (episodes != null && !episodes.isEmpty()) {
                    VideoEpisode firstEpisode = episodes.get(0);
                    if (firstEpisode.getDuration() != null && firstEpisode.getDuration() > 0) {
                        video.setDuration(firstEpisode.getDuration());
                    }
                }
            }
        }

        return videos;
    }
}