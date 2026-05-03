package com.easylive.base.service;

import com.easylive.base.entity.po.MemberCoupon;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface MemberCouponService extends IService<MemberCoupon> {
    List<MemberCoupon> getAvailableCoupons(Integer vipLevel);
    boolean grantCouponToUser(String userId, Integer couponId);
    List<MemberCoupon> getAllActiveCoupons();
}
