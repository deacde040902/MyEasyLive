package com.easylive.base.service;

import com.easylive.base.entity.po.UserConversation;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 用户会话列表 服务类
 * </p>
 *
 * @author licheng
 * @since 2026-04-21
 */
public interface UserConversationService extends IService<UserConversation> {
    UserConversation getOrCreate(String userId, String targetId);
    void updateLastMessage(String userId, String targetId, String lastMessage, java.time.LocalDateTime lastTime);
    void incrementUnread(String userId, String targetId);
    void resetUnread(String userId, String targetId);
    List<UserConversation> getConversationList(String userId);
}
