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
 * 视频评论表
 * </p>
 *
 * @author licheng
 * @since 2026-04-26
 */
@Getter
@Setter
@TableName("video_comment")
public class VideoComment implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "comment_id", type = IdType.AUTO)
    private Long commentId;

    @TableField("video_id")
    private String videoId;

    @TableField("user_id")
    private String userId;

    @TableField("content")
    private String content;

    /**
     * 回复的评论ID
     */
    @TableField("parent_id")
    private Long parentId;

    @TableField("create_time")
    private LocalDateTime createTime;
}
