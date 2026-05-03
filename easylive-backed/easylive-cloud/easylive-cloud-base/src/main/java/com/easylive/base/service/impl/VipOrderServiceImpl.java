package com.easylive.base.service.impl;

import com.easylive.base.service.VipOrderService;
import com.easylive.base.entity.po.VipOrder;
import com.easylive.base.entity.po.VipPackage;
import com.easylive.base.entity.po.UserInfo;
import com.easylive.base.mapper.VipOrderMapper;
import com.easylive.base.mapper.UserInfoMapper;
import com.easylive.base.service.VipPackageService;
import com.easylive.base.service.UserInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class VipOrderServiceImpl extends ServiceImpl<VipOrderMapper, VipOrder> implements VipOrderService {

    private final VipPackageService vipPackageService;
    private final UserInfoService userInfoService;
    private final RedissonClient redissonClient;

    public VipOrderServiceImpl(VipPackageService vipPackageService, UserInfoService userInfoService, 
                               RedissonClient redissonClient) {
        this.vipPackageService = vipPackageService;
        this.userInfoService = userInfoService;
        this.redissonClient = redissonClient;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public VipOrder createOrder(String userId, String packageId) {
        String lockKey = "vip:order:create:" + userId;
        RLock lock = redissonClient.getLock(lockKey);
        
        try {
            if (!lock.tryLock(10, 60, TimeUnit.SECONDS)) {
                throw new RuntimeException("操作过于频繁，请稍后重试");
            }
            
            VipPackage vipPackage = vipPackageService.getPackageById(packageId);
            if (vipPackage == null || vipPackage.getStatus() != 1) {
                throw new RuntimeException("套餐不存在或已下架");
            }

            UserInfo user = userInfoService.getById(userId);
            if (user == null) {
                throw new RuntimeException("用户不存在");
            }

            String orderId = generateOrderId(userId);
            
            VipOrder order = new VipOrder();
            order.setOrderId(orderId);
            order.setUserId(userId);
            order.setPackageId(packageId);
            order.setPackageName(vipPackage.getName());
            order.setPrice(vipPackage.getDiscountPrice() != null ? vipPackage.getDiscountPrice() : vipPackage.getPrice());
            order.setOriginalPrice(vipPackage.getPrice());
            order.setStatus(0);
            order.setPayType(1);
            order.setCreateTime(LocalDateTime.now());
            order.setExpireTime(LocalDateTime.now().plusMinutes(30));

            this.save(order);
            return order;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("创建订单失败");
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean payOrder(String orderId, Integer payType, String transactionId) {
        String lockKey = "vip:order:pay:" + orderId;
        RLock lock = redissonClient.getLock(lockKey);
        
        try {
            if (!lock.tryLock(10, 60, TimeUnit.SECONDS)) {
                throw new RuntimeException("订单处理中，请稍后");
            }

            VipOrder order = this.getById(orderId);
            if (order == null) {
                throw new RuntimeException("订单不存在");
            }

            if (order.getStatus() != 0) {
                throw new RuntimeException("订单状态不正确");
            }

            if (order.getExpireTime().isBefore(LocalDateTime.now())) {
                order.setStatus(3);
                this.updateById(order);
                throw new RuntimeException("订单已过期");
            }

            order.setStatus(1);
            order.setPayType(payType);
            order.setPayTime(LocalDateTime.now());
            this.updateById(order);

            VipPackage vipPackage = vipPackageService.getPackageById(order.getPackageId());
            if (vipPackage != null) {
                userInfoService.updateVipStatus(order.getUserId(), vipPackage.getMonths());
            }

            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("支付失败");
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @Override
    public VipOrder getOrderById(String orderId) {
        return this.getById(orderId);
    }

    @Override
    public List<VipOrder> getOrdersByUserId(String userId) {
        return baseMapper.selectByUserId(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelOrder(String orderId) {
        UpdateWrapper<VipOrder> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("order_id", orderId).eq("status", 0);
        VipOrder order = new VipOrder();
        order.setStatus(2);
        return this.update(order, updateWrapper);
    }

    @Override
    public boolean updateOrderStatus(String orderId, Integer status) {
        UpdateWrapper<VipOrder> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("order_id", orderId);
        VipOrder order = new VipOrder();
        order.setStatus(status);
        return this.update(order, updateWrapper);
    }

    private String generateOrderId(String userId) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        return "VIP" + userId.substring(userId.length() - 6) + timestamp.substring(timestamp.length() - 10) + uuid;
    }
}
