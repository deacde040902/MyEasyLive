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
@TableName("payment_record")
public class PaymentRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "record_id", type = IdType.INPUT)
    private String recordId;

    @TableField("order_id")
    private String orderId;

    @TableField("user_id")
    private String userId;

    @TableField("pay_amount")
    private BigDecimal payAmount;

    @TableField("pay_type")
    private Integer payType;

    @TableField("pay_status")
    private Integer payStatus;

    @TableField("transaction_id")
    private String transactionId;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;
}
