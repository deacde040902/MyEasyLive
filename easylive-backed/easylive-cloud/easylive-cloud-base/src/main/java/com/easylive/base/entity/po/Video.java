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
 * 视频主表
 * </p>
 *
 * @author licheng
 * @since 2026-04-19
 */
@Getter
@Setter
@TableName("video")
public class Video implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 视频ID
     */
    @TableId(value = "video_id", type = IdType.ASSIGN_ID)
    private String videoId;

    /**
     * 发布用户ID
     */
    @TableField("user_id")
    private String userId;

    /**
     * 视频标题
     */
    @TableField("title")
    private String title;

    /**
     * 视频描述
     */
    @TableField("description")
    private String description;

    /**
     * 封面图片文件ID
     */
    @TableField("cover_file_id")
    private String coverFileId;

    /**
     * 分类ID
     */
    @TableField("category_id")
    private Integer categoryId;

    /**
     * 总时长（秒）
     */
    @TableField("duration")
    private Integer duration;

    /**
     * 播放量
     */
    @TableField("play_count")
    private Long playCount;

    /**
     * 点赞数
     */
    @TableField("like_count")
    private Long likeCount;

    /**
     * 评论数
     */
    @TableField("comment_count")
    private Long commentCount;

    /**
     * 状态（0删除 1正常 2审核中）
     */
    @TableField("status")
    private Byte status;

    /**
     * 发布时间
     */
    @TableField("publish_time")
    private LocalDateTime publishTime;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;

    /**
     * 审核拒绝原因
     */
    @TableField("audit_reason")
    private String auditReason;

    /**
     * 是否推荐 0-否 1-是
     */
    @TableField("recommend_flag")
    private Integer recommendFlag;

    /**
     * 视频封面URL（瞬态字段）
     */
    @TableField(exist = false)
    private String cover;

    /**
     * 视频播放URL（瞬态字段）
     */
    @TableField(exist = false)
    private String videoUrl;

    /**
     * 作者名称（瞬态字段）
     */
    @TableField(exist = false)
    private String authorName;

    /**
     * 作者头像（瞬态字段）
     */
    @TableField(exist = false)
    private String authorAvatar;

    /**
     * 作者粉丝数（瞬态字段）
     */
    @TableField(exist = false)
    private Long authorFans;

    /**
     * 是否大视频 0-否 1-是
     */
    @TableField("is_big_video")
    private Integer isBigVideo;
}
