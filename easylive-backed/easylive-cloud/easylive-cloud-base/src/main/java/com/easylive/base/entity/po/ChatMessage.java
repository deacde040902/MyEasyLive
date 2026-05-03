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
 * 聊天消息表
 * </p>
 *
 * @author licheng
 * @since 2026-04-21
 */
@Getter
@Setter
@TableName("chat_message")
public class ChatMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "message_id", type = IdType.AUTO)
    private Long messageId;

    /**
     * 发送者ID
     */
    @TableField("from_user_id")
    private String fromUserId;

    /**
     * 接收者ID
     */
    @TableField("to_user_id")
    private String toUserId;

    /**
     * 消息内容（文本或表情代码）
     */
    @TableField("content")
    private String content;

    /**
     * 消息类型 1文本 2表情 3图片
     */
    @TableField("message_type")
    private Integer messageType;

    /**
     * 图片URL（当type=3时）
     */
    @TableField("image_url")
    private String imageUrl;

    /**
     * 是否已读 0未读 1已读
     */
    @TableField("is_read")
    private Integer isRead;

    /**
     * 状态 1正常 0删除（逻辑删除）2撤回
     */
    @TableField("status")
    private Integer status;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("read_time")
    private LocalDateTime readTime;
}
