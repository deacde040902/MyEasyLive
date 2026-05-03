package com.easylive.web.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.easylive.base.annotation.GlobalInterceptor;
import com.easylive.base.controller.ABaseController;
import com.easylive.base.entity.constants.Constants;
import com.easylive.base.entity.po.RechargeOrder;
import com.easylive.base.entity.po.VipPackage;
import com.easylive.base.entity.vo.ResponseVO;
import com.easylive.base.exception.BusinessException;
import com.easylive.base.service.OrderService;
import com.easylive.base.service.UserInfoService;
import com.easylive.base.service.VipPackageService;
import com.easylive.base.service.VipOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
public class PaymentController extends ABaseController {

    @Autowired
    private VipPackageService vipPackageService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private VipOrderService vipOrderService;

    /**
     * 获取套餐列表
     */
    @GetMapping("/vip/packages")
    @SentinelResource(value = "getVipPackages", blockHandler = "getVipPackagesBlock")
    public ResponseVO<List<VipPackage>> getVipPackages() {
        List<VipPackage> packages = vipPackageService.getActivePackages();
        return getSuccessResponseVO(packages);
    }

    public ResponseVO<List<VipPackage>> getVipPackagesBlock(BlockException e) {
        throw new BusinessException("系统繁忙，请稍后重试");
    }

    /**
     * 创建VIP订单
     */
    @PostMapping("/createVipOrder")
    @GlobalInterceptor
    @SentinelResource(value = "createVipOrder", blockHandler = "createVipOrderBlock")
    public ResponseVO<Map<String, Object>> createVipOrder(@RequestHeader(Constants.REQUEST_HEADER_TOKEN) String token, @RequestBody Map<String, Object> params) {
        String userId = userInfoService.getUserIdByToken(token);
        String packageId = params.get("packageId") != null ? params.get("packageId").toString() : null;
        if (packageId == null) {
            return getServerErrorResponseVO("套餐ID不能为空");
        }
        com.easylive.base.entity.po.VipOrder order = vipOrderService.createOrder(userId, packageId);
        Map<String, Object> result = new HashMap<>();
        result.put("orderId", order.getOrderId());
        result.put("price", order.getPrice());
        result.put("packageName", order.getPackageName());
        return getSuccessResponseVO(result);
    }

    public ResponseVO<Map<String, String>> createVipOrderBlock(BlockException e) {
        throw new BusinessException("请求过于频繁，请稍后再试");
    }

    /**
     * 支付VIP订单
     */
    @PostMapping("/payVipOrder")
    @GlobalInterceptor
    @SentinelResource(value = "payVipOrder", blockHandler = "payVipOrderBlock")
    public ResponseVO<Map<String, Object>> payVipOrder(@RequestHeader(Constants.REQUEST_HEADER_TOKEN) String token, @RequestBody Map<String, Object> params) {
        String orderId = params.get("orderId") != null ? params.get("orderId").toString() : null;
        Integer payType = params.get("payType") != null ? (Integer) params.get("payType") : 1;
        if (orderId == null) {
            return getServerErrorResponseVO("订单ID不能为空");
        }
        // 模拟支付成功
        String transactionId = "TRANS" + System.currentTimeMillis();
        boolean success = vipOrderService.payOrder(orderId, payType, transactionId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        result.put("orderId", orderId);
        return getSuccessResponseVO(result);
    }

    public ResponseVO<Map<String, Object>> payVipOrderBlock(BlockException e) {
        throw new BusinessException("支付服务繁忙，请稍后重试");
    }

    /**
     * 获取VIP状态
     */
    @GetMapping("/vip/status")
    @GlobalInterceptor
    public ResponseVO<Map<String, Object>> getVipStatus(@RequestHeader(Constants.REQUEST_HEADER_TOKEN) String token) {
        String userId = userInfoService.getUserIdByToken(token);
        
        Map<String, Object> result = userInfoService.getUserVipInfo(userId);
        return getSuccessResponseVO(result);
    }

    /**
     * 通过userId获取VIP状态
     */
    @GetMapping("/getVipStatus")
    public ResponseVO<Map<String, Object>> getVipStatusByUserId(@RequestParam String userId) {
        Map<String, Object> result = userInfoService.getUserVipInfo(userId);
        return getSuccessResponseVO(result);
    }

    /**
     * 领取优惠券
     */
    @PostMapping("/claimCoupon")
    @GlobalInterceptor
    public ResponseVO<String> claimCoupon(@RequestHeader(Constants.REQUEST_HEADER_TOKEN) String token, @RequestBody Map<String, Object> params) {
        return getSuccessResponseVO("领取成功");
    }

    /**
     * 兼容旧的支付接口
     */
    @PostMapping("/vip/buy/{packageId}")
    @GlobalInterceptor
    public ResponseVO<Map<String, String>> buyVipPackage(@PathVariable String packageId,
                                                         @RequestHeader(Constants.REQUEST_HEADER_TOKEN) String token) {
        String userId = userInfoService.getUserIdByToken(token);
        String orderNo = orderService.createOrder(userId, packageId, null);
        Map<String, String> result = new HashMap<>();
        result.put("orderId", orderNo);
        return getSuccessResponseVO(result);
    }

    /**
     * 兼容旧的订单查询接口
     */
    @GetMapping("/vip/order/{orderId}")
    public ResponseVO<RechargeOrder> getVipOrder(@PathVariable String orderId) {
        RechargeOrder order = orderService.getOrderByNo(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        return getSuccessResponseVO(order);
    }

    /**
     * 兼容旧的支付接口
     */
    @PostMapping("/vip/pay/{orderId}")
    public ResponseVO<Map<String, Object>> payVipOrderLegacy(@PathVariable String orderId,
                                                               @RequestBody Map<String, Object> params) {
        orderService.payCallback(orderId, "alipay");
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        return getSuccessResponseVO(result);
    }
}
