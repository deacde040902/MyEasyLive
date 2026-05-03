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
 * 用户视频互动表
 * </p>
 *
 * @author licheng
 * @since 2026-04-19
 */
@Getter
@Setter
@TableName("video_interaction")
public class VideoInteraction implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "interaction_id", type = IdType.AUTO)
    private Long interactionId;

    @TableField("user_id")
    private String userId;

    @TableField("video_id")
    private String videoId;

    /**
     * 互动类型（1点赞 2收藏 3投币）
     */
    @TableField("type")
    private Byte type;

    @TableField("create_time")
    private LocalDateTime createTime;
}
