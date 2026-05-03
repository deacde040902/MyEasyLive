package com.easylive.base.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easylive.base.entity.po.ChatMessage;
import com.easylive.base.entity.po.UserConversation;

import java.util.List;

/**
 * <p>
 * 聊天消息表 服务类
 * </p>
 *
 * @author licheng
 * @since 2026-04-21
 */

public interface ChatMessageService extends IService<ChatMessage> {
    /**
     * 发送消息（含规则校验：未回复前只能发一条）
     * @param fromUserId 发送者
     * @param toUserId 接收者
     * @param content 消息内容
     * @param messageType 类型
     * @param imageUrl 图片URL（可选）
     * @return 保存后的消息对象
     */
    ChatMessage sendMessage(String fromUserId, String toUserId, String content, Integer messageType, String imageUrl);

    /**
     * 获取与某人的聊天记录（分页）
     */
    List<ChatMessage> getChatHistory(String userId, String targetId, Integer pageNo, Integer pageSize);

    /**
     * 删除消息（逻辑删除，仅删除自己的视角）
     */
    void deleteMessage(Long messageId, String userId);

    /**
     * 标记某人的所有消息为已读
     */
    void markAsRead(String userId, String targetId);

    /**
     * 获取总未读消息数
     */
    int getTotalUnreadCount(String userId);

    /**
     * 获取每个联系人的未读消息数（用于分组显示）
     */
    List<UserConversation> getUnreadCountGroup(String userId);

    /**
     * 获取最近会话列表（含最后一条消息和未读数）
     */
    List<UserConversation> getConversationList(String userId);

    /**
     * 撤回消息（仅发送者可撤回，且只能撤回自己发送的消息）
     * @param messageId 消息ID
     * @param userId 操作人ID
     */
    void recallMessage(Long messageId, String userId);

    /**
     * 清空与某人的所有聊天记录（对自己隐藏）
     * @param userId 当前用户ID
     * @param targetId 目标用户ID
     */
    void clearChatHistory(String userId, String targetId);

    /**
     * 删除会话（对自己隐藏整个会话）
     * @param userId 当前用户ID
     * @param targetId 目标用户ID
     */
    void deleteConversation(String userId, String targetId);
    
}