package com.easylive.web.controller;

import com.easylive.base.controller.ABaseController;
import com.easylive.base.entity.vo.ResponseVO;
import com.easylive.base.service.CouponService;
import com.easylive.base.entity.po.Coupon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 优惠券相关控制器
 */
@RestController
@RequestMapping("/api/coupon")
public class CouponController extends ABaseController {

    @Autowired
    private CouponService couponService;

    /**
     * 获取可用优惠券
     */
    @PostMapping("/list")
    public ResponseVO<List<Coupon>> getCoupons() {
        List<Coupon> coupons = couponService.getAvailableCoupons();
        return getSuccessResponseVO(coupons);
    }

    /**
     * 领取优惠券
     */
    @PostMapping("/receive/{couponId}")
    public ResponseVO<Coupon> receiveCoupon(@PathVariable String couponId, 
                                           @RequestHeader("token") String token) {
        // 从token中获取用户ID
        String userId = getUserIdFromToken(token);
        Coupon coupon = couponService.receiveCoupon(userId, couponId);
        return getSuccessResponseVO(coupon);
    }

    /**
     * 使用优惠券
     */
    @PostMapping("/use/{couponCode}")
    public ResponseVO<Coupon> useCoupon(@PathVariable String couponCode, 
                                       @RequestParam String orderId, 
                                       @RequestHeader("token") String token) {
        // 从token中获取用户ID
        String userId = getUserIdFromToken(token);
        Coupon coupon = couponService.useCoupon(userId, couponCode, orderId);
        return getSuccessResponseVO(coupon);
    }

    /**
     * 从token中获取用户ID
     */
    private String getUserIdFromToken(String token) {
        // 这里应该从Redis中获取用户信息
        // 暂时返回模拟数据
        return "user_123";
    }
}
