package com.easylive.base.service.impl;

import com.easylive.base.service.PaymentService;
import com.easylive.base.entity.po.PaymentRecord;
import com.easylive.base.mapper.PaymentRecordMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class PaymentServiceImpl extends ServiceImpl<PaymentRecordMapper, PaymentRecord> implements PaymentService {

    @Value("${payment.sign.secret:easy_live_payment_secret_2024}")
    private String signSecret;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PaymentRecord createPaymentRecord(String orderId, String userId, BigDecimal amount, Integer payType) {
        PaymentRecord record = new PaymentRecord();
        record.setRecordId(UUID.randomUUID().toString().replace("-", ""));
        record.setOrderId(orderId);
        record.setUserId(userId);
        record.setPayAmount(amount);
        record.setPayType(payType);
        record.setPayStatus(0);
        record.setCreateTime(LocalDateTime.now());
        record.setUpdateTime(LocalDateTime.now());

        this.save(record);
        return record;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updatePaymentStatus(String recordId, Integer status, String transactionId) {
        UpdateWrapper<PaymentRecord> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("record_id", recordId);
        
        PaymentRecord record = new PaymentRecord();
        record.setPayStatus(status);
        record.setTransactionId(transactionId);
        record.setUpdateTime(LocalDateTime.now());
        
        return this.update(record, updateWrapper);
    }

    @Override
    public boolean verifyPaymentSign(Map<String, String> params) {
        if (params == null || !params.containsKey("sign")) {
            return false;
        }

        String receivedSign = params.get("sign");
        Map<String, String> sortedParams = new HashMap<>();
        
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (!"sign".equals(entry.getKey()) && entry.getValue() != null && !entry.getValue().isEmpty()) {
                sortedParams.put(entry.getKey(), entry.getValue());
            }
        }

        StringBuilder sb = new StringBuilder();
        sortedParams.keySet().stream().sorted().forEach(key -> {
            sb.append(key).append("=").append(sortedParams.get(key)).append("&");
        });
        
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
        }
        
        sb.append("&key=").append(signSecret);

        String calculatedSign = md5(sb.toString());
        return calculatedSign.equalsIgnoreCase(receivedSign);
    }

    @Override
    public PaymentRecord getPaymentByOrderId(String orderId) {
        List<PaymentRecord> records = baseMapper.selectByOrderId(orderId);
        return records.isEmpty() ? null : records.get(0);
    }

    private String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 algorithm not found", e);
        }
    }

    public String generateSign(Map<String, String> params) {
        Map<String, String> sortedParams = new HashMap<>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (entry.getValue() != null && !entry.getValue().isEmpty()) {
                sortedParams.put(entry.getKey(), entry.getValue());
            }
        }

        StringBuilder sb = new StringBuilder();
        sortedParams.keySet().stream().sorted().forEach(key -> {
            sb.append(key).append("=").append(sortedParams.get(key)).append("&");
        });
        
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
        }
        
        sb.append("&key=").append(signSecret);
        return md5(sb.toString());
    }
}
