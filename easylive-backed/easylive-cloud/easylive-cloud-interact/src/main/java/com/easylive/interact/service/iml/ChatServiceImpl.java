package com.easylive.interact.service.iml;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easylive.base.entity.po.ChatMessage;
import com.easylive.base.entity.po.UserInfo;
import com.easylive.base.exception.BusinessException;
import com.easylive.base.mapper.ChatMessageMapper;
import com.easylive.base.service.ChatMessageService;
import com.easylive.base.service.UserInfoService;
import com.easylive.interact.service.ChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ChatServiceImpl extends ServiceImpl<ChatMessageMapper, ChatMessage> implements ChatService {

    @Resource
    private UserInfoService userInfoService;

    @Resource
    private ChatMessageService chatMessageService;

    @Override
    @Transactional
    public ChatMessage sendMessage(String senderId, String receiverId, String content, Integer messageType) {
        return chatMessageService.sendMessage(senderId, receiverId, content, messageType, null);
    }

    @Override
    public List<ChatMessage> getChatHistory(String userId1, String userId2) {
        return chatMessageService.getChatHistory(userId1, userId2, 1, 1000);
    }

    @Override
    public List<ChatMessage> getUnreadMessages(String userId) {
        LambdaQueryWrapper<ChatMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ChatMessage::getToUserId, userId)
                .eq(ChatMessage::getIsRead, 0)
                .eq(ChatMessage::getStatus, 1)
                .orderByDesc(ChatMessage::getCreateTime);
        return list(wrapper);
    }

    @Override
    public void markAsRead(Long messageId) {
        ChatMessage msg = getById(messageId);
        if (msg != null) {
            msg.setIsRead(1);
            msg.setReadTime(LocalDateTime.now());
            updateById(msg);
        }
    }

    @Override
    public List<Map<String, Object>> getSessionList(String currentUserId) {
        LambdaQueryWrapper<ChatMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w -> w.eq(ChatMessage::getFromUserId, currentUserId)
                        .or()
                        .eq(ChatMessage::getToUserId, currentUserId))
                .orderByDesc(ChatMessage::getCreateTime);
        List<ChatMessage> allMsgs = list(wrapper);

        Map<String, ChatMessage> latestMsgMap = new HashMap<>();
        for (ChatMessage msg : allMsgs) {
            String contactId = currentUserId.equals(msg.getFromUserId()) ?
                    msg.getToUserId() : msg.getFromUserId();
            latestMsgMap.putIfAbsent(contactId, msg);
        }

        List<Map<String, Object>> sessions = new ArrayList<>();
        for (Map.Entry<String, ChatMessage> entry : latestMsgMap.entrySet()) {
            String contactId = entry.getKey();
            ChatMessage lastMsg = entry.getValue();

            UserInfo contactUser = userInfoService.getUserInfoByUserId(contactId);
            if (contactUser == null) continue;

            Map<String, Object> sessionMap = new HashMap<>();
            sessionMap.put("contactUserId", contactId);
            sessionMap.put("contactNickName", contactUser.getNickName());
            sessionMap.put("contactAvatar", contactUser.getAvatarPath());
            sessionMap.put("lastMessage", lastMsg.getContent());
            sessionMap.put("lastMessageType", lastMsg.getMessageType());
            sessionMap.put("lastTime", lastMsg.getCreateTime());

            long unread = count(new LambdaQueryWrapper<ChatMessage>()
                    .eq(ChatMessage::getFromUserId, contactId)
                    .eq(ChatMessage::getToUserId, currentUserId)
                    .eq(ChatMessage::getIsRead, 0)
                    .eq(ChatMessage::getStatus, 1));
            sessionMap.put("unreadCount", (int) unread);

            sessions.add(sessionMap);
        }

        sessions.sort((a, b) -> {
            LocalDateTime timeA = (LocalDateTime) a.get("lastTime");
            LocalDateTime timeB = (LocalDateTime) b.get("lastTime");
            return timeB.compareTo(timeA);
        });
        return sessions;
    }

    @Override
    public List<ChatMessage> getMessageList(String userId) {
        LambdaQueryWrapper<ChatMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ChatMessage::getFromUserId, userId)
                .orderByDesc(ChatMessage::getCreateTime);
        return list(wrapper);
    }

    @Override
    public List<Map<String, Object>> getSessionListMap(String userId) {
        return getSessionList(userId);
    }

    @Override
    public List<Map<String, Object>> getChatHistoryMap(String userId1, String userId2) {
        List<ChatMessage> messages = getChatHistory(userId1, userId2);
        return messages.stream().map(msg -> {
            Map<String, Object> map = new HashMap<>();
            map.put("messageId", msg.getMessageId());
            map.put("fromUserId", msg.getFromUserId());
            map.put("toUserId", msg.getToUserId());
            map.put("content", msg.getContent());
            map.put("messageType", msg.getMessageType());
            map.put("isRead", msg.getIsRead());
            map.put("createTime", msg.getCreateTime());
            return map;
        }).collect(Collectors.toList());
    }

    @Override
    public void sendMessage(Map<String, Object> message) {
        String senderId = (String) message.get("senderId");
        String receiverId = (String) message.get("receiverId");
        String content = (String) message.get("content");
        String imageUrl = (String) message.get("imageUrl");
        Object messageTypeObj = message.get("messageType");
        Integer messageType = (messageTypeObj instanceof Integer) ? 
            (Integer) messageTypeObj : (messageTypeObj != null ? Integer.parseInt(messageTypeObj.toString()) : 1);
        chatMessageService.sendMessage(senderId, receiverId, content, messageType, imageUrl);
    }

    @Override
    public void markRead(String currentUserId, String contactUserId) {
        chatMessageService.markAsRead(currentUserId, contactUserId);
    }

    public void deleteMessage(Long messageId, String currentUserId) {
        ChatMessage msg = getById(messageId);
        if (msg == null) {
            throw new BusinessException("消息不存在");
        }
        if (!msg.getFromUserId().equals(currentUserId) && !msg.getToUserId().equals(currentUserId)) {
            throw new BusinessException("无权删除");
        }
        msg.setStatus(0);
        updateById(msg);
    }
}