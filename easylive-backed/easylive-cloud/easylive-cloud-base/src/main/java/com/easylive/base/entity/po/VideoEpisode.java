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
 * 视频分P表
 * </p>
 *
 * @author licheng
 * @since 2026-04-19
 */
@Getter
@Setter
@TableName("video_episode")
public class VideoEpisode implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 分P ID
     */
    @TableId(value = "episode_id", type = IdType.ASSIGN_ID)
    private String episodeId;

    /**
     * 所属视频ID
     */
    @TableField("video_id")
    private String videoId;

    /**
     * 分P标题
     */
    @TableField("title")
    private String title;

    /**
     * 分P序号（从1开始）
     */
    @TableField("episode_num")
    private Integer episodeNum;

    /**
     * 视频文件ID
     */
    @TableField("file_id")
    private String fileId;

    /**
     * 本P时长
     */
    @TableField("duration")
    private Integer duration;

    /**
     * m3u8地址（HLS）
     */
    @TableField("m3u8_url")
    private String m3u8Url;

    @TableField("create_time")
    private LocalDateTime createTime;
}
