package com.easylive.base.service;

import com.easylive.base.entity.po.VipOrder;
import com.baomidou.mybatisplus.extension.service.IService;

import java.math.BigDecimal;
import java.util.List;

public interface VipOrderService extends IService<VipOrder> {
    VipOrder createOrder(String userId, String packageId);
    boolean updateOrderStatus(String orderId, Integer status);
    boolean payOrder(String orderId, Integer payType, String transactionId);
    VipOrder getOrderById(String orderId);
    List<VipOrder> getOrdersByUserId(String userId);
    boolean cancelOrder(String orderId);
}
