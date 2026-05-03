package com.easylive.base.config;

import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Sentinel 限流规则配置
 */
@Component
public class SentinelFlowRules implements CommandLineRunner {

    @Override
    public void run(String... args) {
        // 定义限流规则
        List<FlowRule> rules = new ArrayList<>();

        // 1. 支付接口限流规则
        FlowRule payRule = new FlowRule();
        payRule.setResource("pay");
        payRule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        payRule.setCount(100);  // QPS 为 100
        payRule.setControlBehavior(RuleConstant.CONTROL_BEHAVIOR_DEFAULT);
        rules.add(payRule);

        // 2. 创建订单接口限流规则
        FlowRule createOrderRule = new FlowRule();
        createOrderRule.setResource("createOrder");
        createOrderRule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        createOrderRule.setCount(200);  // QPS 为 200
        createOrderRule.setControlBehavior(RuleConstant.CONTROL_BEHAVIOR_DEFAULT);
        rules.add(createOrderRule);

        // 3. 优惠券领取接口限流规则
        FlowRule receiveCouponRule = new FlowRule();
        receiveCouponRule.setResource("receiveCoupon");
        receiveCouponRule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        receiveCouponRule.setCount(50);  // QPS 为 50
        receiveCouponRule.setControlBehavior(RuleConstant.CONTROL_BEHAVIOR_DEFAULT);
        rules.add(receiveCouponRule);

        // 4. VIP开通接口限流规则
        FlowRule openVipRule = new FlowRule();
        openVipRule.setResource("openVip");
        openVipRule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        openVipRule.setCount(100);  // QPS 为 100
        openVipRule.setControlBehavior(RuleConstant.CONTROL_BEHAVIOR_DEFAULT);
        rules.add(openVipRule);

        // 加载规则
        FlowRuleManager.loadRules(rules);
    }
}
