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
 * 
 * </p>
 *
 * @author licheng
 * @since 2026-04-21
 */
@Getter
@Setter
@TableName("play_history")
public class PlayHistory implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "history_id", type = IdType.AUTO)
    private Long historyId;

    @TableField("user_id")
    private String userId;

    @TableField("video_id")
    private String videoId;

    @TableField("episode_id")
    private String episodeId;

    @TableField("watch_time")
    private Integer watchTime;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;
}
