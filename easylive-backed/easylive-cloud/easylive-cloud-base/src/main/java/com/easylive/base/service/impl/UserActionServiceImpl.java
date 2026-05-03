package com.easylive.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.easylive.base.entity.po.UserAction;
import com.easylive.base.entity.po.Video;
import com.easylive.base.exception.BusinessException;
import com.easylive.base.mapper.UserActionMapper;
import com.easylive.base.service.UserActionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easylive.base.service.VideoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * <p>
 * 用户行为记录表 服务实现类
 * </p>
 *
 * @author licheng
 * @since 2026-04-20
 */
@Service
public class UserActionServiceImpl extends ServiceImpl<UserActionMapper, UserAction> implements UserActionService {

    @Resource
    private UserActionMapper userActionMapper;
    @Resource
    private VideoService videoService;

    @Override
    @Transactional
    public void doAction(UserAction userAction) {
        if (userAction.getUserId() == null || userAction.getVideoId() == null || userAction.getActionType() == null) {
            throw new BusinessException("参数缺失");
        }
        // 检查是否已存在相同类型的动作（幂等处理）
        LambdaQueryWrapper<UserAction> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserAction::getUserId, userAction.getUserId())
                .eq(UserAction::getVideoId, userAction.getVideoId())
                .eq(UserAction::getActionType, userAction.getActionType());
        UserAction existing = this.getOne(wrapper);
        if (existing != null) {
            // 如果已存在，且 actionCount 为 0 则删除，否则更新数量
            if (userAction.getActionCount() == null || userAction.getActionCount() == 0) {
                this.removeById(existing.getActionId());
                return;
            } else {
                existing.setActionCount(userAction.getActionCount());
                existing.setCreateTime(LocalDateTime.now());
                this.updateById(existing);
                return;
            }
        }
        // 新增
        userAction.setCreateTime(LocalDateTime.now());
        if (userAction.getActionCount() == null) {
            userAction.setActionCount(1);
        }
        this.save(userAction);
    }

    @Override
    public void like(String videoId, String userId) {
        // 检查是否已点赞
        LambdaQueryWrapper<UserAction> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserAction::getUserId, userId)
                .eq(UserAction::getVideoId, videoId)
                .eq(UserAction::getActionType, "like");
        if (userActionMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("已经点过赞了");
        }

        // 插入点赞记录
        UserAction action = new UserAction();
        action.setUserId(userId);
        action.setVideoId(videoId);
        action.setActionType("like");
        action.setActionCount(1);
        action.setCreateTime(LocalDateTime.now());
        userActionMapper.insert(action);

        // 视频点赞数+1
        videoService.lambdaUpdate()
                .eq(Video::getVideoId, videoId)
                .setSql("like_count = like_count + 1")
                .update();
    }

    @Override
    public void unlike(String videoId, String userId) {
        LambdaQueryWrapper<UserAction> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserAction::getUserId, userId)
                .eq(UserAction::getVideoId, videoId)
                .eq(UserAction::getActionType, "like");
        UserAction action = userActionMapper.selectOne(wrapper);
        if (action != null) {
            userActionMapper.deleteById(action.getActionId());
            videoService.lambdaUpdate()
                    .eq(Video::getVideoId, videoId)
                    .setSql("like_count = like_count - 1")
                    .update();
        }
    }

    @Override
    public boolean isLiked(String videoId, String userId) {
        LambdaQueryWrapper<UserAction> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserAction::getUserId, userId)
                .eq(UserAction::getVideoId, videoId)
                .eq(UserAction::getActionType, "like");
        return userActionMapper.selectCount(wrapper) > 0;
    }
}
