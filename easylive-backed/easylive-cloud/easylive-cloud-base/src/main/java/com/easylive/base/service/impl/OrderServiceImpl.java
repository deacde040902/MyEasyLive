package com.easylive.base.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easylive.base.mapper.RechargeOrderMapper;
import com.easylive.base.entity.po.RechargeOrder;
import com.easylive.base.service.OrderService;
import com.easylive.base.service.VipPackageService;
import com.easylive.base.service.CouponService;
import com.easylive.base.entity.po.VipPackage;
import com.easylive.base.entity.po.Coupon;
import com.easylive.base.exception.BusinessException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 订单服务实现类
 */
@Service
public class OrderServiceImpl extends ServiceImpl<RechargeOrderMapper, RechargeOrder> implements OrderService {

    @Autowired
    private VipPackageService vipPackageService;

    @Autowired
    private CouponService couponService;

    @Override
    @Transactional
    public String createOrder(String userId, String packageId, String couponCode) {
        // 1. 查询套餐信息
        VipPackage vipPackage = vipPackageService.getPackageById(packageId);
        if (vipPackage == null) {
            throw new BusinessException("套餐不存在");
        }

        // 2. 计算金额
        BigDecimal originalAmount = vipPackage.getPrice();
        BigDecimal discountAmount = BigDecimal.ZERO;
        if (couponCode != null && !couponCode.isEmpty()) {
            // 这里应该调用优惠券服务获取优惠券信息并计算折扣
            // 暂时简化处理
            discountAmount = new BigDecimal(5);
        }
        BigDecimal actualAmount = originalAmount.subtract(discountAmount);

        // 3. 创建订单
        RechargeOrder order = new RechargeOrder();
        order.setId(UUID.randomUUID().toString());
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setPackageId(packageId);
        order.setCouponId(couponCode);
        order.setOriginalAmount(originalAmount);
        order.setDiscountAmount(discountAmount);
        order.setActualAmount(actualAmount);
        order.setPayStatus(0); // 待支付
        order.setCreateTime(LocalDateTime.now());

        this.save(order);
        return order.getOrderNo();
    }

    @Override
    @Transactional
    public void payCallback(String orderNo, String payMethod) {
        // 1. 查询订单
        RechargeOrder order = this.getOrderByNo(orderNo);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        // 2. 检查订单状态
        if (order.getPayStatus() == 1) {
            return; // 已支付，无需处理
        }

        // 3. 更新订单状态
        order.setPayStatus(1); // 已支付
        order.setPayTime(LocalDateTime.now());
        order.setPayMethod(payMethod);
        this.updateById(order);

        // 4. 处理充值逻辑
        // 这里应该调用充值服务处理VIP开通
    }

    @Override
    public RechargeOrder getOrderByNo(String orderNo) {
        QueryWrapper<RechargeOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_no", orderNo);
        return this.getOne(queryWrapper);
    }

    private String generateOrderNo() {
        return "ORDER" + System.currentTimeMillis() + (int)(Math.random() * 1000);
    }
}
