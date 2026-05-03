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
 * 用户收藏表
 * </p>
 *
 * @author licheng
 * @since 2026-04-22
 */
@Getter
@Setter
@TableName("user_collection")
public class UserCollection implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "collection_id", type = IdType.AUTO)
    private Long collectionId;

    @TableField("user_id")
    private String userId;

    @TableField("video_id")
    private String videoId;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("episodeId")
    private String episodeId;
}
