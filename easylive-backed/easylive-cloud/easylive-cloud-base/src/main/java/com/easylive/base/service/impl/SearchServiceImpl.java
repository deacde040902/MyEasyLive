package com.easylive.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.easylive.base.entity.po.SearchKeyword;
import com.easylive.base.entity.po.UserInfo;
import com.easylive.base.entity.po.Video;
import com.easylive.base.entity.po.VideoEpisode;
import com.easylive.base.mapper.SearchKeywordMapper;
import com.easylive.base.mapper.VideoMapper;
import com.easylive.base.service.SearchService;
import com.easylive.base.service.UserInfoService;
import com.easylive.base.service.VideoEpisodeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
public class SearchServiceImpl implements SearchService {

    @Resource
    private VideoMapper videoMapper;
    @Resource
    private SearchKeywordMapper searchKeywordMapper;
    @Resource
    private UserInfoService userInfoService;
    @Resource
    private VideoEpisodeService videoEpisodeService;

    @Override
    public List<Video> searchVideo(String keyword, Integer pageNo, Integer pageSize, String sortType) {
        if (!StringUtils.hasText(keyword)) {
            return Collections.emptyList();
        }
        recordSearchKeyword(keyword);

        Page<Video> page = new Page<>(pageNo, pageSize);
        LambdaQueryWrapper<Video> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(Video::getTitle, keyword)
                .or().like(Video::getDescription, keyword)
                .or().inSql(Video::getVideoId, "SELECT video_id FROM video_tag WHERE tag_id IN (SELECT tag_id FROM tag WHERE tag_name LIKE '%" + keyword + "%')")
                .eq(Video::getStatus, 1);
        
        // 根据排序类型排序
        if ("playCount".equals(sortType)) {
            wrapper.orderByDesc(Video::getPlayCount);
        } else if ("publishTime".equals(sortType)) {
            wrapper.orderByDesc(Video::getPublishTime);
        } else if ("danmaku".equals(sortType)) {
            // 按评论数排序（没有弹幕数字段）
            wrapper.orderByDesc(Video::getCommentCount);
        } else if ("favorites".equals(sortType)) {
            // 按点赞数排序（没有收藏数字段）
            wrapper.orderByDesc(Video::getLikeCount);
        } else {
            // 默认按播放量排序
            wrapper.orderByDesc(Video::getPlayCount);
        }
        
        List<Video> videos = videoMapper.selectPage(page, wrapper).getRecords();
        
        // 填充作者信息、封面和duration
        for (Video video : videos) {
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
        
        return videos;
    }

    @Override
    public List<SearchKeyword> getHotKeywords() {
        LambdaQueryWrapper<SearchKeyword> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(SearchKeyword::getSearchCount)
                .last("limit 10");
        return searchKeywordMapper.selectList(wrapper);
    }

    @Override
    @Transactional
    public void recordSearchKeyword(String keyword) {
        if (!StringUtils.hasText(keyword)) return;
        LambdaQueryWrapper<SearchKeyword> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SearchKeyword::getKeyword, keyword);
        SearchKeyword exist = searchKeywordMapper.selectOne(wrapper);
        if (exist == null) {
            exist = new SearchKeyword();
            exist.setKeyword(keyword);
            exist.setSearchCount(1);
            exist.setUpdateTime(LocalDateTime.now());
            searchKeywordMapper.insert(exist);
        } else {
            exist.setSearchCount(exist.getSearchCount() + 1);
            exist.setUpdateTime(LocalDateTime.now());
            searchKeywordMapper.updateById(exist);
        }
    }
}