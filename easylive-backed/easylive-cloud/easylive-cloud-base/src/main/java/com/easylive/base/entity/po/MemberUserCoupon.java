package com.easylive.base.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TableName("member_user_coupon")
public class MemberUserCoupon implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "user_coupon_id", type = IdType.ASSIGN_ID)
    private String userCouponId;

    @TableField("user_id")
    private String userId;

    @TableField("coupon_id")
    private Integer couponId;

    @TableField("coupon_name")
    private String couponName;

    @TableField("coupon_type")
    private Integer couponType;

    @TableField("discount_value")
    private BigDecimal discountValue;

    @TableField("min_amount")
    private BigDecimal minAmount;

    @TableField("status")
    private Integer status;

    @TableField("receive_time")
    private LocalDateTime receiveTime;

    @TableField("use_time")
    private LocalDateTime useTime;

    @TableField("expire_time")
    private LocalDateTime expireTime;
}
