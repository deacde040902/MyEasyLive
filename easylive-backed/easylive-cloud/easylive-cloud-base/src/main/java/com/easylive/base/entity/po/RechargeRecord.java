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
@TableName("recharge_record")
public class RechargeRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    @TableField("user_id")
    private String userId;

    @TableField("order_id")
    private String orderId;

    @TableField("amount")
    private BigDecimal amount;

    @TableField("months")
    private Integer months;

    @TableField("before_vip_expire")
    private LocalDateTime beforeVipExpire;

    @TableField("after_vip_expire")
    private LocalDateTime afterVipExpire;

    @TableField("create_time")
    private LocalDateTime createTime;
}
