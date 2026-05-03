package com.easylive.base.service.impl;

import com.easylive.base.service.MemberCouponService;
import com.easylive.base.entity.po.MemberCoupon;
import com.easylive.base.entity.po.MemberUserCoupon;
import com.easylive.base.mapper.MemberCouponMapper;
import com.easylive.base.mapper.MemberUserCouponMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class MemberCouponServiceImpl extends ServiceImpl<MemberCouponMapper, MemberCoupon> implements MemberCouponService {

    private final MemberUserCouponMapper memberUserCouponMapper;
    private final RedissonClient redissonClient;

    public MemberCouponServiceImpl(MemberUserCouponMapper memberUserCouponMapper, RedissonClient redissonClient) {
        this.memberUserCouponMapper = memberUserCouponMapper;
        this.redissonClient = redissonClient;
    }

    @Override
    public List<MemberCoupon> getAvailableCoupons(Integer vipLevel) {
        if (vipLevel == null || vipLevel < 1) {
            return new ArrayList<>();
        }
        return baseMapper.selectAvailableCoupons(vipLevel);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean grantCouponToUser(String userId, Integer couponId) {
        String lockKey = "coupon:grant:" + couponId + ":" + userId;
        RLock lock = redissonClient.getLock(lockKey);
        
        try {
            if (!lock.tryLock(10, 60, TimeUnit.SECONDS)) {
                throw new RuntimeException("领取过于频繁，请稍后重试");
            }

            MemberCoupon coupon = this.getById(couponId);
            if (coupon == null || coupon.getStatus() != 1) {
                throw new RuntimeException("优惠券不存在或已下架");
            }

            QueryWrapper<MemberUserCoupon> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId).eq("coupon_id", couponId);
            long existingCount = memberUserCouponMapper.selectCount(queryWrapper);
            if (existingCount > 0) {
                throw new RuntimeException("您已领取过该优惠券");
            }

            if (coupon.getTotalCount() != null && coupon.getUsedCount() != null) {
                UpdateWrapper<MemberCoupon> updateWrapper = new UpdateWrapper<>();
                updateWrapper.eq("coupon_id", couponId)
                            .le("used_count", coupon.getTotalCount() - 1);
                MemberCoupon updateCoupon = new MemberCoupon();
                updateCoupon.setUsedCount(coupon.getUsedCount() + 1);
                boolean updated = this.update(updateCoupon, updateWrapper);
                if (!updated) {
                    throw new RuntimeException("优惠券已被领完");
                }
            }

            MemberUserCoupon userCoupon = new MemberUserCoupon();
            userCoupon.setUserCouponId(UUID.randomUUID().toString().replace("-", ""));
            userCoupon.setUserId(userId);
            userCoupon.setCouponId(couponId);
            userCoupon.setCouponName(coupon.getCouponName());
            userCoupon.setCouponType(coupon.getCouponType());
            userCoupon.setDiscountValue(coupon.getDiscountValue());
            userCoupon.setMinAmount(coupon.getMinAmount());
            userCoupon.setStatus(1);
            userCoupon.setReceiveTime(LocalDateTime.now());
            userCoupon.setExpireTime(coupon.getEndTime());

            memberUserCouponMapper.insert(userCoupon);
            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("领取失败");
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @Override
    public List<MemberCoupon> getAllActiveCoupons() {
        QueryWrapper<MemberCoupon> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", 1);
        return this.list(queryWrapper);
    }
}
