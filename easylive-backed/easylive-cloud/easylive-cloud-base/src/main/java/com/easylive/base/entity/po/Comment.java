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
 * 评论表
 * </p>
 *
 * @author licheng
 * @since 2026-04-21
 */
@Getter
@Setter
@TableName("comment")
public class Comment implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "comment_id", type = IdType.AUTO)
    private Long commentId;

    /**
     * 视频ID
     */
    @TableField("video_id")
    private String videoId;

    /**
     * 评论用户ID
     */
    @TableField("user_id")
    private String userId;

    /**
     * 评论内容
     */
    @TableField("content")
    private String content;

    /**
     * 回复的评论ID（一级评论为NULL）
     */
    @TableField("reply_comment_id")
    private Long replyCommentId;

    /**
     * 评论图片路径
     */
    @TableField("img_path")
    private String imgPath;

    /**
     * 是否置顶（0否 1是）
     */
    @TableField("top_flag")
    private Boolean topFlag;

    /**
     * 点赞数
     */
    @TableField("like_count")
    private Integer likeCount;

    /**
     * 状态（0删除 1正常）
     */
    @TableField("status")
    private Boolean status;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;
}
