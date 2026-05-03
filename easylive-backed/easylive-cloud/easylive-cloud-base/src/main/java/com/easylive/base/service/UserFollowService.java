package com.easylive.base.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easylive.base.entity.po.UserFollow;
import java.util.List;

/**
 * 用户关注服务
 */
public interface UserFollowService extends IService<UserFollow> {
    
    /**
     * 关注用户
     * @param followerId 关注者ID
     * @param followingId 被关注者ID
     * @return 是否成功
     */
    boolean followUser(String followerId, String followingId);
    
    /**
     * 取消关注
     * @param followerId 关注者ID
     * @param followingId 被关注者ID
     * @return 是否成功
     */
    boolean unfollowUser(String followerId, String followingId);
    
    /**
     * 获取用户关注列表
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页大小
     * @return 关注列表
     */
    List<UserFollow> getFollowList(String userId, Integer page, Integer size);
    
    /**
     * 获取用户粉丝列表
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页大小
     * @return 粉丝列表
     */
    List<UserFollow> getFanList(String userId, Integer page, Integer size);
    
    /**
     * 检查是否已关注
     * @param followerId 关注者ID
     * @param followingId 被关注者ID
     * @return 是否已关注
     */
    boolean isFollowing(String followerId, String followingId);
    
    /**
     * 获取关注数量
     * @param userId 用户ID
     * @return 关注数量
     */
    long getFollowCount(String userId);
    
    /**
     * 获取粉丝数量
     * @param userId 用户ID
     * @return 粉丝数量
     */
    long getFanCount(String userId);

    Long countFansByUserId(String userId);
}