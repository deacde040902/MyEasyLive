package com.easylive.web.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.easylive.base.controller.ABaseController;
import com.easylive.base.entity.po.RechargeOrder;
import com.easylive.base.entity.vo.ResponseVO;
import com.easylive.base.exception.BusinessException;
import com.easylive.base.service.VipPackageService;
import com.easylive.base.entity.po.VipPackage;
import com.easylive.base.service.OrderService;
import com.easylive.base.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 充值相关控制器
 */
@RestController
@RequestMapping("/api/recharge")
public class RechargeController extends ABaseController {

    @Autowired
    private VipPackageService vipPackageService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private CouponService couponService;

    /**
     * 获取套餐列表
     */
    @PostMapping("/packages")
    @SentinelResource(value = "getPackages", blockHandler = "getPackagesBlock")
    public ResponseVO<List<VipPackage>> getPackages() {
        List<VipPackage> packages = vipPackageService.getActivePackages();
        return getSuccessResponseVO(packages);
    }

    public ResponseVO<List<VipPackage>> getPackagesBlock(BlockException e) {
        throw new BusinessException("系统繁忙，请稍后重试");
    }

    /**
     * 创建充值订单
     */
    @PostMapping("/create")
    @SentinelResource(value = "createOrder", blockHandler = "createOrderBlock")
    public ResponseVO<Map<String, String>> createOrder(@RequestParam String packageId,
                                                       @RequestParam(required = false) String couponCode,
                                                       @RequestHeader("token") String token) {
        String userId = getUserIdFromToken(token);
        String orderNo = orderService.createOrder(userId, packageId, couponCode);
        Map<String, String> result = new HashMap<>();
        result.put("orderNo", orderNo);
        return getSuccessResponseVO(result);
    }

    public ResponseVO<Map<String, String>> createOrderBlock(@RequestParam String packageId,
                                                            @RequestParam(required = false) String couponCode,
                                                            @RequestHeader("token") String token,
                                                            BlockException e) {
        throw new BusinessException("请求过于频繁，请稍后再试");
    }

    /**
     * 获取支付参数
     */
    @PostMapping("/pay")
    @SentinelResource(value = "pay", blockHandler = "payBlock")
    public ResponseVO<Map<String, String>> pay(@RequestParam String orderNo,
                                               @RequestParam String payMethod) {
        Map<String, String> payParams = new HashMap<>();
        payParams.put("orderNo", orderNo);
        payParams.put("payMethod", payMethod);
        payParams.put("payUrl", "https://payment.example.com/pay?orderNo=" + orderNo);
        return getSuccessResponseVO(payParams);
    }

    public ResponseVO<Map<String, String>> payBlock(@RequestParam String orderNo,
                                                    @RequestParam String payMethod,
                                                    BlockException e) {
        throw new BusinessException("支付服务繁忙，请稍后重试");
    }

    /**
     * 支付回调
     */
    @PostMapping("/callback/{method}")
    public String payCallback(@PathVariable String method, HttpServletRequest request) {
        orderService.payCallback(request.getParameter("orderNo"), method);
        return "success";
    }

    /**
     * 查询订单状态
     */
    @GetMapping("/order/{orderNo}")
    @SentinelResource(value = "getOrderStatus", blockHandler = "getOrderStatusBlock")
    public ResponseVO<Map<String, Object>> getOrderStatus(@PathVariable String orderNo) {
        RechargeOrder order = orderService.getOrderByNo(orderNo);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        Map<String, Object> orderInfo = new HashMap<>();
        orderInfo.put("orderNo", order.getOrderNo());
        orderInfo.put("status", order.getPayStatus());
        orderInfo.put("amount", order.getActualAmount());
        orderInfo.put("createTime", order.getCreateTime());
        return getSuccessResponseVO(orderInfo);
    }

    public ResponseVO<Map<String, Object>> getOrderStatusBlock(@PathVariable String orderNo, BlockException e) {
        throw new BusinessException("系统繁忙，请稍后重试");
    }

    /**
     * 从token中获取用户ID
     */
    private String getUserIdFromToken(String token) {
        // 请实现真实的token解析逻辑
        // 例如调用 redisComponent.getTokenUserInfoDto(token).getUserId()
        return "user_123";
    }
}