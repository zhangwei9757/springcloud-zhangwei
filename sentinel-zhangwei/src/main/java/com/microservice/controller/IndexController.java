package com.microservice.controller;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zw
 * @date 2020-11-26
 * <p>
 */
@RestController
@Api(value = "测试", tags = "测试")
public class IndexController {

    @GetMapping(value = "/hello")
    @ApiOperation(value = "hello", tags = "测试")
    public String hello() {
        return "正常响应";
    }

    @PostMapping(value = "/index")
    @ApiOperation(value = "index", tags = "测试")
    @ApiImplicitParams({
//            @ApiImplicitParam(name = "a", required = false, value = "查询条件", dataType = "MessageProtocolPoJo.MessageProtocol", paramType = "body"),
    })
    public String index() {
        initFlowRules();
        int index = 1;
        while (index++ <= 100) {
            Entry entry = null;
            try {
                entry = SphU.entry("HelloWorld");
                /*您的业务逻辑 - 开始*/
                System.out.println("hello world");
                /*您的业务逻辑 - 结束*/
            } catch (BlockException e1) {
                /*流控逻辑处理 - 开始*/
                System.out.println("block!");
                /*流控逻辑处理 - 结束*/
            } finally {
                if (entry != null) {
                    entry.exit();
                }
            }
        }
        return "index";
    }

    private static void initFlowRules() {
        List<FlowRule> rules = new ArrayList<>();
        FlowRule rule = new FlowRule();
        rule.setResource("HelloWorld");
        rule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        // Set limit QPS to 20.
        rule.setCount(20);
        rules.add(rule);
        FlowRuleManager.loadRules(rules);
    }
}
