package com.easylive.base.service;

import com.easylive.base.entity.po.UserCollection;
import com.baomidou.mybatisplus.extension.service.IService;
import com.easylive.base.entity.po.Video;

import java.util.List;

/**
 * <p>
 * 用户收藏表 服务类
 * </p>
 *
 * @author licheng
 * @since 2026-04-22
 */
public interface UserCollectionService extends IService<UserCollection> {
    void collect(String userId, String videoId);
    void uncollect(String userId, String videoId);
    boolean isCollected(String videoId, String userId);
    List<Video> getUserCollections(String userId, Integer pageNo, Integer pageSize);
    void addCollection(String userId, String videoId, String episodeId);
    void removeCollection(String userId, String videoId);
    List<UserCollection> getCollectionList(String userId, Integer pageNo, Integer pageSize);
}
