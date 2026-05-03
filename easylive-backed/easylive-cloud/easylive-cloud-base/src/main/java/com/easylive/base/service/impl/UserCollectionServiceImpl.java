package com.easylive.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.easylive.base.entity.po.UserCollection;
import com.easylive.base.entity.po.Video;
import com.easylive.base.exception.BusinessException;
import com.easylive.base.mapper.UserCollectionMapper;
import com.easylive.base.service.UserCollectionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easylive.base.service.VideoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 用户收藏表 服务实现类
 * </p>
 *
 * @author licheng
 * @since 2026-04-22
 */
@Service
public class UserCollectionServiceImpl extends ServiceImpl<UserCollectionMapper, UserCollection> implements UserCollectionService {
    @Resource
    private VideoService videoService;

    @Resource
    private UserCollectionMapper userCollectionMapper;


    @Override
    @Transactional
    public void uncollect(String userId, String videoId) {
        this.remove(new LambdaQueryWrapper<UserCollection>()
                .eq(UserCollection::getUserId, userId)
                .eq(UserCollection::getVideoId, videoId));
    }

    @Override
    public List<Video> getUserCollections(String userId, Integer pageNo, Integer pageSize) {
        Page<UserCollection> page = new Page<>(pageNo, pageSize);
        LambdaQueryWrapper<UserCollection> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserCollection::getUserId, userId).orderByDesc(UserCollection::getCreateTime);
        Page<UserCollection> result = this.page(page, wrapper);
        List<String> videoIds = result.getRecords().stream().map(UserCollection::getVideoId).collect(Collectors.toList());
        if (videoIds.isEmpty()) return Collections.emptyList();
        return videoService.listByIds(videoIds);
    }
    @Override
    @Transactional
    public void addCollection(String userId, String videoId, String episodeId) {
        LambdaQueryWrapper<UserCollection> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserCollection::getUserId, userId).eq(UserCollection::getVideoId, videoId);
        UserCollection exist = userCollectionMapper.selectOne(wrapper);
        if (exist != null) {
            throw new BusinessException("已经收藏过该视频");
        }
        UserCollection collection = new UserCollection();
        collection.setUserId(userId);
        collection.setVideoId(videoId);
        collection.setEpisodeId(episodeId);
        collection.setCreateTime(LocalDateTime.now());
        userCollectionMapper.insert(collection);
    }

    @Override
    @Transactional
    public void removeCollection(String userId, String videoId) {
        LambdaQueryWrapper<UserCollection> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserCollection::getUserId, userId).eq(UserCollection::getVideoId, videoId);
        userCollectionMapper.delete(wrapper);
    }

    @Override
    public List<UserCollection> getCollectionList(String userId, Integer pageNo, Integer pageSize) {
        Page<UserCollection> page = new Page<>(pageNo, pageSize);
        LambdaQueryWrapper<UserCollection> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserCollection::getUserId, userId)
                .orderByDesc(UserCollection::getCreateTime);
        return userCollectionMapper.selectPage(page, wrapper).getRecords();
    }

    @Override
    public void collect(String videoId, String userId) {
        if (isCollected(videoId, userId)) throw new BusinessException("已收藏");
        UserCollection collection = new UserCollection();
        collection.setUserId(userId);
        collection.setVideoId(videoId);
        collection.setCreateTime(LocalDateTime.now());
        this.save(collection);
        videoService.lambdaUpdate()
                .eq(Video::getVideoId, videoId)
                .setSql("collect_count = collect_count + 1")
                .update();
    }


    @Override
    public boolean isCollected(String videoId, String userId) {
        LambdaQueryWrapper<UserCollection> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserCollection::getUserId, userId)
                .eq(UserCollection::getVideoId, videoId);
        return this.count(wrapper) > 0;
    }
}
