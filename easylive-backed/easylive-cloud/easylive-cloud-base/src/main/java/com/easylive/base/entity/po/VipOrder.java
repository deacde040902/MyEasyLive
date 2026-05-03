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
@TableName("vip_order")
public class VipOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "order_id", type = IdType.INPUT)
    private String orderId;

    @TableField("user_id")
    private String userId;

    @TableField("package_id")
    private String packageId;

    @TableField("package_name")
    private String packageName;

    @TableField("price")
    private BigDecimal price;

    @TableField("original_price")
    private BigDecimal originalPrice;

    @TableField("status")
    private Integer status;

    @TableField("pay_type")
    private Integer payType;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("pay_time")
    private LocalDateTime payTime;

    @TableField("expire_time")
    private LocalDateTime expireTime;

    @TableField("transaction_id")
    private String transactionId;
}
