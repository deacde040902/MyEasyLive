package com.easylive.base.entity.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ChatSessionVO {
    private String contactUserId;       // 对话对方用户ID
    private String contactNickName;     // 对方昵称
    private String contactAvatar;       // 对方头像
    private String lastMessage;         // 最后一条消息内容
    private Integer lastMessageType;    // 最后一条消息类型
    private LocalDateTime lastTime;     // 最后消息时间
    private Integer unreadCount;        // 未读消息数
}