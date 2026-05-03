package com.easylive.base.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户关注关系表
 */
@Data
@TableName("user_follow")
public class UserFollow {
    /**
     * 主键ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    
    /**
     * 关注者ID
     */
    private String followerId;
    
    /**
     * 被关注者ID
     */
    private String followingId;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 状态：0-正常 1-已取消
     */
    private Integer status;
}