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
 * 文件资源表
 * </p>
 *
 * @author licheng
 * @since 2026-04-19
 */
@Getter
@Setter
@TableName("file_resource")
public class FileResource implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 文件ID
     */
    @TableId(value = "file_id", type = IdType.ASSIGN_ID)
    private String fileId;

    /**
     * 上传用户ID
     */
    @TableField("user_id")
    private String userId;

    /**
     * 原始文件名
     */
    @TableField("original_name")
    private String originalName;

    /**
     * 存储路径
     */
    @TableField("file_path")
    private String filePath;

    /**
     * 文件大小（字节）
     */
    @TableField("file_size")
    private Long fileSize;

    /**
     * 文件类型（image/video/other）
     */
    @TableField("file_type")
    private String fileType;

    /**
     * MIME类型
     */
    @TableField("mime_type")
    private String mimeType;

    /**
     * 图片/视频宽度
     */
    @TableField("width")
    private Integer width;

    /**
     * 图片/视频高度
     */
    @TableField("height")
    private Integer height;

    /**
     * 视频时长（秒）
     */
    @TableField("duration")
    private Integer duration;

    /**
     * 状态（0删除 1正常）
     */
    @TableField("status")
    private Byte status;

    @TableField("create_time")
    private LocalDateTime createTime;
}
