package com.easylive.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easylive.base.entity.po.ChatMessage;
import com.easylive.base.entity.po.UserConversation;
import com.easylive.base.exception.BusinessException;
import com.easylive.base.mapper.ChatMessageMapper;
import com.easylive.base.mapper.UserConversationMapper;
import com.easylive.base.service.ChatMessageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 聊天消息表 服务实现类
 * </p>
 *
 * @author licheng
 * @since 2026-04-21
 */

@Service
public class ChatMessageServiceImpl extends ServiceImpl<ChatMessageMapper, ChatMessage> implements ChatMessageService {

    @Resource
    private UserConversationMapper userConversationMapper;

    @Override
    @Transactional
    public ChatMessage sendMessage(String fromUserId, String toUserId, String content, Integer messageType, String imageUrl) {
        if (fromUserId.equals(toUserId)) {
            throw new BusinessException("不能给自己发消息");
        }
        if (messageType == null) messageType = 1;
        if (messageType == 1 && !StringUtils.hasText(content)) {
            throw new BusinessException("消息内容不能为空");
        }
        if (messageType == 3 && !StringUtils.hasText(imageUrl)) {
            throw new BusinessException("图片URL不能为空");
        }

        // 规则校验：未回复前只能发一条
        long fromToCount = this.count(new LambdaQueryWrapper<ChatMessage>()
                .eq(ChatMessage::getFromUserId, fromUserId)
                .eq(ChatMessage::getToUserId, toUserId)
                .eq(ChatMessage::getStatus, 1));
        long toFromCount = this.count(new LambdaQueryWrapper<ChatMessage>()
                .eq(ChatMessage::getFromUserId, toUserId)
                .eq(ChatMessage::getToUserId, fromUserId)
                .eq(ChatMessage::getStatus, 1));

        if (fromToCount > 0 && toFromCount == 0) {
            throw new BusinessException("对方尚未回复，你暂时不能继续发送消息");
        }

        // 保存消息
        ChatMessage message = new ChatMessage();
        message.setFromUserId(fromUserId);
        message.setToUserId(toUserId);
        message.setContent(content);
        message.setMessageType(messageType);
        message.setImageUrl(imageUrl);
        message.setIsRead(0);
        message.setStatus(1);
        message.setCreateTime(LocalDateTime.now());
        this.save(message);

        // 更新双方会话列表
        updateConversation(fromUserId, toUserId, content, message.getCreateTime());
        updateConversation(toUserId, fromUserId, content, message.getCreateTime());

        // 增加对方的未读计数
        UserConversation toConv = getUserConversation(toUserId, fromUserId);
        if (toConv == null) {
            toConv = new UserConversation();
            toConv.setUserId(toUserId);
            toConv.setTargetId(fromUserId);
            toConv.setLastMessage(content);
            toConv.setLastMessageTime(message.getCreateTime());
            toConv.setUnreadCount(1);
            userConversationMapper.insert(toConv);
        } else {
            toConv.setLastMessage(content);
            toConv.setLastMessageTime(message.getCreateTime());
            toConv.setUnreadCount(toConv.getUnreadCount() + 1);
            userConversationMapper.updateById(toConv);
        }

        return message;
    }

    private void updateConversation(String userId, String targetId, String lastMsg, LocalDateTime time) {
        UserConversation conv = getUserConversation(userId, targetId);
        if (conv == null) {
            conv = new UserConversation();
            conv.setUserId(userId);
            conv.setTargetId(targetId);
            conv.setLastMessage(lastMsg);
            conv.setLastMessageTime(time);
            conv.setUnreadCount(0);
            userConversationMapper.insert(conv);
        } else {
            conv.setLastMessage(lastMsg);
            conv.setLastMessageTime(time);
            userConversationMapper.updateById(conv);
        }
    }

    private UserConversation getUserConversation(String userId, String targetId) {
        LambdaQueryWrapper<UserConversation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserConversation::getUserId, userId)
                .eq(UserConversation::getTargetId, targetId);
        return userConversationMapper.selectOne(wrapper);
    }

    @Override
    public List<ChatMessage> getChatHistory(String userId, String targetId, Integer pageNo, Integer pageSize) {
        Page<ChatMessage> page = new Page<>(pageNo, pageSize);
        LambdaQueryWrapper<ChatMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w -> w.eq(ChatMessage::getFromUserId, userId).eq(ChatMessage::getToUserId, targetId)
                        .or().eq(ChatMessage::getFromUserId, targetId).eq(ChatMessage::getToUserId, userId))
                .eq(ChatMessage::getStatus, 1)
                .orderByDesc(ChatMessage::getCreateTime);
        Page<ChatMessage> result = this.page(page, wrapper);
        return result.getRecords();
    }

    @Override
    @Transactional
    public void deleteMessage(Long messageId, String userId) {
        ChatMessage message = this.getById(messageId);
        if (message == null || message.getStatus() != 1) {
            throw new BusinessException("消息不存在");
        }
        if (!message.getFromUserId().equals(userId) && !message.getToUserId().equals(userId)) {
            throw new BusinessException("无权删除");
        }
        message.setStatus(0);
        this.updateById(message);
        // 可选：更新会话列表最后一条消息（略）
    }

    @Override
    @Transactional
    public void markAsRead(String userId, String targetId) {
        // 简化实现
    }

    @Override
    public int getTotalUnreadCount(String userId) {
        return 0;
    }

    @Override
    public List<UserConversation> getUnreadCountGroup(String userId) {
        return null;
    }

    @Override
    public List<UserConversation> getConversationList(String userId) {
        return null;
    }

    @Override
    @Transactional
    public void recallMessage(Long messageId, String userId) {
        // 简化实现
    }

    @Override
    @Transactional
    public void clearChatHistory(String userId, String targetId) {
        // 简化实现
    }

    @Override
    @Transactional
    public void deleteConversation(String userId, String targetId) {
        // 简化实现
    }
}
