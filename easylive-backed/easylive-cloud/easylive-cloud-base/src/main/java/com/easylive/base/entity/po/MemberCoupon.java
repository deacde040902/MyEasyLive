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
@TableName("member_coupon")
public class MemberCoupon implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "coupon_id", type = IdType.AUTO)
    private Integer couponId;

    @TableField("coupon_name")
    private String couponName;

    @TableField("coupon_type")
    private Integer couponType;

    @TableField("discount_value")
    private BigDecimal discountValue;

    @TableField("min_amount")
    private BigDecimal minAmount;

    @TableField("total_count")
    private Integer totalCount;

    @TableField("used_count")
    private Integer usedCount;

    @TableField("start_time")
    private LocalDateTime startTime;

    @TableField("end_time")
    private LocalDateTime endTime;

    @TableField("vip_level")
    private Integer vipLevel;

    @TableField("status")
    private Integer status;

    @TableField("create_time")
    private LocalDateTime createTime;
}
