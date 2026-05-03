package com.easylive.base.service;

import com.easylive.base.entity.po.PaymentRecord;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

public interface PaymentService extends IService<PaymentRecord> {
    PaymentRecord createPaymentRecord(String orderId, String userId, java.math.BigDecimal amount, Integer payType);
    boolean updatePaymentStatus(String recordId, Integer status, String transactionId);
    boolean verifyPaymentSign(Map<String, String> params);
    PaymentRecord getPaymentByOrderId(String orderId);
}
