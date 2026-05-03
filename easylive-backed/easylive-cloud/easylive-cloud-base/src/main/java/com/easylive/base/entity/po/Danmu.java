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
 * 弹幕表
 * </p>
 *
 * @author licheng
 * @since 2026-04-20
 */
@Getter
@Setter
@TableName("danmu")
public class Danmu implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "danmu_id", type = IdType.AUTO)
    private Long danmuId;

    /**
     * 视频ID
     */
    @TableField("video_id")
    private String videoId;

    /**
     * 分P ID（可选）
     */
    @TableField("episode_id")
    private String episodeId;

    /**
     * 发送用户ID（未登录可为空）
     */
    @TableField("user_id")
    private String userId;

    /**
     * 弹幕内容
     */
    @TableField("content")
    private String content;

    /**
     * 弹幕出现时间（秒，相对于视频）
     */
    @TableField("time")
    private Integer time;

    /**
     * 弹幕颜色
     */
    @TableField("color")
    private String color;

    /**
     * 弹幕类型（1滚动 2顶部 3底部）
     */
    @TableField("type")
    private Integer type;

    @TableField("create_time")
    private LocalDateTime createTime;
}
