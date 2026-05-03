package com.easylive.interact.service;

import com.easylive.base.entity.po.ChatMessage;

import java.util.List;
import java.util.Map;

public interface ChatService {

    /**
     * 发送消息
     */
    ChatMessage sendMessage(String senderId, String receiverId, String content, Integer messageType);

    /**
     * 获取与某个用户的聊天历史
     */
    List<ChatMessage> getChatHistory(String userId1, String userId2);

    /**
     * 获取未读消息列表
     */
    List<ChatMessage> getUnreadMessages(String userId);

    /**
     * 标记消息为已读
     */
    void markAsRead(Long messageId);

    /**
     * 获取会话列表（带最新消息和用户信息）
     */
    List<Map<String, Object>> getSessionList(String currentUserId);

    /**
     * 获取用户的所有消息（管理员或特定需求）
     */
    List<ChatMessage> getMessageList(String userId);

    /**
     * 获取消息列表（会话列表，返回前端可直接使用的格式）
     */
    List<Map<String, Object>> getSessionListMap(String userId);

    /**
     * 获取与指定用户的聊天历史
     */
    List<Map<String, Object>> getChatHistoryMap(String userId1, String userId2);

    /**
     * 发送消息（参数从 Map 中解析）
     */
    void sendMessage(Map<String, Object> message);

    /**
     * 标记当前用户与某联系人的全部未读消息为已读
     */
    void markRead(String currentUserId, String contactUserId);
}