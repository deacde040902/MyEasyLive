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
@TableName("recharge_order")
public class RechargeOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    @TableField("order_no")
    private String orderNo;

    @TableField("user_id")
    private String userId;

    @TableField("package_id")
    private String packageId;

    @TableField("coupon_id")
    private String couponId;

    @TableField("original_amount")
    private BigDecimal originalAmount;

    @TableField("discount_amount")
    private BigDecimal discountAmount;

    @TableField("actual_amount")
    private BigDecimal actualAmount;

    @TableField("pay_status")
    private Integer payStatus;

    @TableField("pay_time")
    private LocalDateTime payTime;

    @TableField("pay_method")
    private String payMethod;

    @TableField("create_time")
    private LocalDateTime createTime;
}
