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
 * 视频标签关联表
 * </p>
 *
 * @author licheng
 * @since 2026-04-19
 */
@Getter
@Setter
@TableName("video_tag")
public class VideoTag implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "video_id", type = IdType.ASSIGN_ID)
    private String videoId;

    @TableField("tag_id")
    private Integer tagId;
}
