package com.easylive.base.service;

import com.easylive.base.entity.po.Coupon;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author licheng
 * @since 2026-04-23
 */
public interface CouponService extends IService<Coupon> {
    List<Coupon> getAvailableCoupons();     // 获取可用优惠券
    Coupon receiveCoupon(String userId, String couponId); // 领取优惠券
    Coupon useCoupon(String userId, String couponCode, String orderId); // 使用优惠券
}
