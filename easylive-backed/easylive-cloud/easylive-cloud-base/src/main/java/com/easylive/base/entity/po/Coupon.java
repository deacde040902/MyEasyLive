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
@TableName("coupon")
public class Coupon implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    @TableField("coupon_name")
    private String couponName;

    @TableField("coupon_type")
    private Integer couponType;

    @TableField("min_amount")
    private BigDecimal minAmount;

    @TableField("discount_amount")
    private BigDecimal discountAmount;

    @TableField("valid_days")
    private Integer validDays;

    @TableField("total_count")
    private Integer totalCount;

    @TableField("remain_count")
    private Integer remainCount;

    @TableField("status")
    private Integer status;

    @TableField("create_time")
    private LocalDateTime createTime;
}
