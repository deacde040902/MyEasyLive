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
 * 搜索热词表
 * </p>
 *
 * @author licheng
 * @since 2026-04-22
 */
@Getter
@Setter
@TableName("search_keyword")
public class SearchKeyword implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "keyword_id", type = IdType.AUTO)
    private Long keywordId;

    @TableField("keyword")
    private String keyword;

    @TableField("search_count")
    private Integer searchCount;

    @TableField("update_time")
    private LocalDateTime updateTime;
}
