package com.easylive.base.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easylive.base.entity.po.RechargeOrder;

/**
 * 订单服务接口
 */
public interface OrderService extends IService<RechargeOrder> {
    /**
     * 创建订单
     */
    String createOrder(String userId, String packageId, String couponCode);

    /**
     * 支付回调
     */
    void payCallback(String orderNo, String payMethod);

    /**
     * 根据订单号查询订单
     */
    RechargeOrder getOrderByNo(String orderNo);
}
