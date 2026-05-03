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
 * @since 2026-04-23
 */
@Getter
@Setter
@TableName("user_coupon")
public class UserCoupon implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    @TableField("user_id")
    private String userId;

    @TableField("coupon_id")
    private String couponId;

    @TableField("coupon_code")
    private String couponCode;

    @TableField("status")
    private Integer status;

    @TableField("receive_time")
    private LocalDateTime receiveTime;

    @TableField("use_time")
    private LocalDateTime useTime;

    @TableField("expire_time")
    private LocalDateTime expireTime;
}
