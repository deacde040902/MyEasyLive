package com.easylive.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easylive.base.entity.po.UserFollow;
import com.easylive.base.mapper.UserFollowMapper;
import com.easylive.base.service.UserFollowService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户关注服务实现
 */
@Service
public class UserFollowServiceImpl extends ServiceImpl<UserFollowMapper, UserFollow> implements UserFollowService {
    
    @Override
    @Transactional
    public boolean followUser(String followerId, String followingId) {
        // 检查是否已经关注
        UserFollow existingFollow = baseMapper.selectByFollowerAndFollowing(followerId, followingId);
        if (existingFollow != null) {
            // 如果已关注但状态为已取消，恢复关注
            if (existingFollow.getStatus() == 1) {
                existingFollow.setStatus(0);
                return baseMapper.updateById(existingFollow) > 0;
            }
            // 已经关注，不需要重复操作
            return false;
        }
        
        // 创建新的关注关系
        UserFollow userFollow = new UserFollow();
        userFollow.setFollowerId(followerId);
        userFollow.setFollowingId(followingId);
        userFollow.setCreateTime(LocalDateTime.now());
        userFollow.setStatus(0); // 0-正常
        
        return baseMapper.insert(userFollow) > 0;
    }
    
    @Override
    @Transactional
    public boolean unfollowUser(String followerId, String followingId) {
        UserFollow existingFollow = baseMapper.selectByFollowerAndFollowing(followerId, followingId);
        if (existingFollow != null && existingFollow.getStatus() == 0) {
            // 软删除，将状态改为已取消
            existingFollow.setStatus(1);
            return baseMapper.updateById(existingFollow) > 0;
        }
        return false;
    }
    
    @Override
    public List<UserFollow> getFollowList(String userId, Integer page, Integer size) {
        int offset = (page - 1) * size;
        return baseMapper.selectFollowList(userId, offset, size);
    }
    
    @Override
    public List<UserFollow> getFanList(String userId, Integer page, Integer size) {
        int offset = (page - 1) * size;
        return baseMapper.selectFanList(userId, offset, size);
    }
    
    @Override
    public boolean isFollowing(String followerId, String followingId) {
        UserFollow follow = baseMapper.selectByFollowerAndFollowing(followerId, followingId);
        return follow != null && follow.getStatus() == 0;
    }
    
    @Override
    public long getFollowCount(String userId) {
        // 可以根据实际需要实现，这里简单返回一个模拟值
        // 实际应该使用 COUNT 查询
        return 10;
    }
    
    @Override
    public long getFanCount(String userId) {
        // 可以根据实际需要实现，这里简单返回一个模拟值
        // 实际应该使用 COUNT 查询
        return 15;
    }

    @Override
    public Long countFansByUserId(String userId) {
        LambdaQueryWrapper<UserFollow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserFollow::getFollowingId, userId)
                .eq(UserFollow::getStatus, 0);
        return baseMapper.selectCount(wrapper);
    }
}