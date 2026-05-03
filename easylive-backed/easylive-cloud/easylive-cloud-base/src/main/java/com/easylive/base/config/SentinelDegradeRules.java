package com.easylive.base.config;

import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.alibaba.csp.sentinel.slots.block.degrade.circuitbreaker.CircuitBreakerStrategy;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Sentinel 熔断规则配置
 */
@Component
public class SentinelDegradeRules implements CommandLineRunner {

    @Override
    public void run(String... args) {
        // 定义熔断规则
        List<DegradeRule> rules = new ArrayList<>();

        // 1. 支付接口熔断规则
        DegradeRule payRule = new DegradeRule();
        payRule.setResource("pay");
        payRule.setGrade(CircuitBreakerStrategy.ERROR_RATIO.getType());
        payRule.setCount(0.5);  // 异常比例为 50%
        payRule.setSlowRatioThreshold(1.0);  // 慢调用比例为 100%
        payRule.setMinRequestAmount(5);  // 最小请求数为 5
        payRule.setStatIntervalMs(10000);  // 统计时长为 10 秒
        payRule.setTimeWindow(10);  // 熔断时长为 10 秒
        rules.add(payRule);

        // 2. 创建订单接口熔断规则
        DegradeRule createOrderRule = new DegradeRule();
        createOrderRule.setResource("createOrder");
        createOrderRule.setGrade(CircuitBreakerStrategy.ERROR_RATIO.getType());
        createOrderRule.setCount(0.5);  // 异常比例为 50%
        createOrderRule.setSlowRatioThreshold(1.0);  // 慢调用比例为 100%
        createOrderRule.setMinRequestAmount(5);  // 最小请求数为 5
        createOrderRule.setStatIntervalMs(10000);  // 统计时长为 10 秒
        createOrderRule.setTimeWindow(10);  // 熔断时长为 10 秒
        rules.add(createOrderRule);

        // 3. 优惠券领取接口熔断规则
        DegradeRule receiveCouponRule = new DegradeRule();
        receiveCouponRule.setResource("receiveCoupon");
        receiveCouponRule.setGrade(CircuitBreakerStrategy.ERROR_RATIO.getType());
        receiveCouponRule.setCount(0.3);  // 异常比例为 30%
        receiveCouponRule.setSlowRatioThreshold(1.0);  // 慢调用比例为 100%
        receiveCouponRule.setMinRequestAmount(5);  // 最小请求数为 5
        receiveCouponRule.setStatIntervalMs(10000);  // 统计时长为 10 秒
        receiveCouponRule.setTimeWindow(10);  // 熔断时长为 10 秒
        rules.add(receiveCouponRule);

        // 4. VIP开通接口熔断规则
        DegradeRule openVipRule = new DegradeRule();
        openVipRule.setResource("openVip");
        openVipRule.setGrade(CircuitBreakerStrategy.ERROR_RATIO.getType());
        openVipRule.setCount(0.5);  // 异常比例为 50%
        openVipRule.setSlowRatioThreshold(1.0);  // 慢调用比例为 100%
        openVipRule.setMinRequestAmount(5);  // 最小请求数为 5
        openVipRule.setStatIntervalMs(10000);  // 统计时长为 10 秒
        openVipRule.setTimeWindow(10);  // 熔断时长为 10 秒
        rules.add(openVipRule);

        // 加载规则
        DegradeRuleManager.loadRules(rules);
    }
}
