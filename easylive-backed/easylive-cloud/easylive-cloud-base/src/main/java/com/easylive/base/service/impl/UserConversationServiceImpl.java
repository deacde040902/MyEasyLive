package com.easylive.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.easylive.base.entity.po.UserConversation;
import com.easylive.base.mapper.UserConversationMapper;
import com.easylive.base.service.UserConversationService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 用户会话列表 服务实现类
 * </p>
 *
 * @author licheng
 * @since 2026-04-21
 */
@Service
public class UserConversationServiceImpl extends ServiceImpl<UserConversationMapper, UserConversation> implements UserConversationService {

    @Override
    public UserConversation getOrCreate(String userId, String targetId) {
        LambdaQueryWrapper<UserConversation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserConversation::getUserId, userId).eq(UserConversation::getTargetId, targetId);
        UserConversation exist = this.getOne(wrapper);
        if (exist == null) {
            exist = new UserConversation();
            exist.setUserId(userId);
            exist.setTargetId(targetId);
            exist.setUnreadCount(0);
            exist.setLastMessageTime(LocalDateTime.now());
            this.save(exist);
        }
        return exist;
    }

    @Override
    @Transactional
    public void updateLastMessage(String userId, String targetId, String lastMessage, LocalDateTime lastTime) {
        UserConversation conv = getOrCreate(userId, targetId);
        conv.setLastMessage(lastMessage);
        conv.setLastMessageTime(lastTime);
        this.updateById(conv);
    }

    @Override
    @Transactional
    public void incrementUnread(String userId, String targetId) {
        UserConversation conv = getOrCreate(userId, targetId);
        conv.setUnreadCount(conv.getUnreadCount() + 1);
        this.updateById(conv);
    }

    @Override
    @Transactional
    public void resetUnread(String userId, String targetId) {
        LambdaQueryWrapper<UserConversation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserConversation::getUserId, userId).eq(UserConversation::getTargetId, targetId);
        UserConversation conv = this.getOne(wrapper);
        if (conv != null && conv.getUnreadCount() > 0) {
            conv.setUnreadCount(0);
            this.updateById(conv);
        }
    }

    @Override
    public List<UserConversation> getConversationList(String userId) {
        LambdaQueryWrapper<UserConversation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserConversation::getUserId, userId)
                .orderByDesc(UserConversation::getLastMessageTime);
        return this.list(wrapper);
    }
}
