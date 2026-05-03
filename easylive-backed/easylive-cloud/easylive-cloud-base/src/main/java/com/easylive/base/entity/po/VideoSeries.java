package com.easylive.base.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 视频系列表
 * </p>
 *
 * @author licheng
 * @since 2026-04-21
 */
@Getter
@Setter
@TableName("video_series")
public class VideoSeries implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "series_id", type = IdType.AUTO)
    private Long seriesId;

    /**
     * 创建者ID
     */
    @TableField("user_id")
    private String userId;

    /**
     * 系列名称
     */
    @TableField("series_name")
    private String seriesName;

    /**
     * 排序顺序
     */
    @TableField("sort_order")
    private Integer sortOrder;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;

    @TableField(exist = false)
    private List<Video> videos;
}
