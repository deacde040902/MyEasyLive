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
 * 用户行为记录表
 * </p>
 *
 * @author licheng
 * @since 2026-04-20
 */
@Getter
@Setter
@TableName("user_action")
public class UserAction implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "action_id", type = IdType.AUTO)
    private Long actionId;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private String userId;

    /**
     * 视频ID
     */
    @TableField("video_id")
    private String videoId;

    /**
     * 动作类型：like/dislike/coin/favorite/share
     */
    @TableField("action_type")
    private String actionType;

    /**
     * 动作数量（如投币数量）
     */
    @TableField("action_count")
    private Integer actionCount;

    /**
     * 关联的评论ID（可选）
     */
    @TableField("comment_id")
    private Long commentId;

    @TableField("create_time")
    private LocalDateTime createTime;
}
