package com.easylive.base.service.impl;

import com.easylive.base.service.CouponService;
import com.easylive.base.entity.po.Coupon;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easylive.base.mapper.CouponMapper;
import com.easylive.base.exception.BusinessException;
import com.easylive.base.entity.po.UserCoupon;
import com.easylive.base.mapper.UserCouponMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author licheng
 * @since 2026-04-23
 */
@Service
public class CouponServiceImpl extends ServiceImpl<CouponMapper, Coupon> implements CouponService {

    @Autowired
    private UserCouponMapper userCouponMapper;

    @Override
    public List<Coupon> getAvailableCoupons() {
        QueryWrapper<Coupon> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", 1)
                .gt("remain_count", 0);
        return this.list(queryWrapper);
    }

    @Override
    @Transactional
    public Coupon receiveCoupon(String userId, String couponId) {
        // 1. 查询优惠券是否存在
        Coupon coupon = this.getById(couponId);
        if (coupon == null) {
            throw new BusinessException("优惠券不存在");
        }

        // 2. 检查优惠券是否可用
        if (coupon.getStatus() != 1) {
            throw new BusinessException("优惠券已下架");
        }
        if (coupon.getRemainCount() <= 0) {
            throw new BusinessException("优惠券已领完");
        }

        // 3. 检查用户是否已领取过
        QueryWrapper<UserCoupon> userCouponQuery = new QueryWrapper<>();
        userCouponQuery.eq("user_id", userId)
                .eq("coupon_id", couponId);
        if (userCouponMapper.selectCount(userCouponQuery) > 0) {
            throw new BusinessException("优惠券已领取过");
        }

        // 4. 领取优惠券
        UserCoupon userCoupon = new UserCoupon();
        userCoupon.setUserId(userId);
        userCoupon.setCouponId(couponId);
        userCoupon.setCouponCode(generateCouponCode());
        userCoupon.setStatus(0); // 未使用
        userCoupon.setReceiveTime(LocalDateTime.now());
        userCoupon.setExpireTime(LocalDateTime.now().plusDays(coupon.getValidDays()));
        userCouponMapper.insert(userCoupon);

        // 5. 减少优惠券库存
        coupon.setRemainCount(coupon.getRemainCount() - 1);
        this.updateById(coupon);

        return coupon;
    }

    @Override
    @Transactional
    public Coupon useCoupon(String userId, String couponCode, String orderId) {
        // 1. 查询用户优惠券
        QueryWrapper<UserCoupon> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                .eq("coupon_code", couponCode)
                .eq("status", 0);
        UserCoupon userCoupon = userCouponMapper.selectOne(queryWrapper);
        if (userCoupon == null) {
            throw new BusinessException("优惠券不存在或已使用");
        }

        // 2. 检查是否过期
        if (userCoupon.getExpireTime().isBefore(LocalDateTime.now())) {
            userCoupon.setStatus(2); // 已过期
            userCouponMapper.updateById(userCoupon);
            throw new BusinessException("优惠券已过期");
        }

        // 3. 标记为已使用
        userCoupon.setStatus(1); // 已使用
        userCoupon.setUseTime(LocalDateTime.now());
        userCouponMapper.updateById(userCoupon);

        // 4. 返回优惠券信息
        return this.getById(userCoupon.getCouponId());
    }

    private String generateCouponCode() {
        // 生成优惠券码
        return "COUPON" + System.currentTimeMillis() + (int)(Math.random() * 1000);
    }
}
