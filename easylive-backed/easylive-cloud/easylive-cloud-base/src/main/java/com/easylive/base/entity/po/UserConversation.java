package com.easylive.base.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 用户会话列表
 * </p>
 *
 * @author licheng
 * @since 2026-04-21
 */
@Getter
@Setter
@TableName("user_conversation")
public class UserConversation implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private String userId;

    /**
     * 对话对方用户ID
     */
    @TableField("target_id")
    private String targetId;

    /**
     * 最后一条消息内容
     */
    @TableField("last_message")
    private String lastMessage;

    @TableField("last_message_time")
    private LocalDateTime lastMessageTime;

    @TableField("unread_count")
    private Integer unreadCount;
}
