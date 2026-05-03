package com.easylive.base.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 视频系列关联表
 * </p>
 *
 * @author licheng
 * @since 2026-04-21
 */
@Getter
@Setter
@TableName("video_series_rel")
public class VideoSeriesRel implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("video_id")
    private String videoId;

    @TableField("series_id")
    private Long seriesId;

    @TableField("sort_order")
    private Integer sortOrder;
}
