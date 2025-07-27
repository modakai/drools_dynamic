package com.example.drools.controller;

import com.example.drools.dto.ApiResponse;
import com.example.drools.dto.TestResult;
import com.example.drools.dto.TestRuleRequest;
import com.example.drools.dto.ValidateRuleRequest;
import com.example.drools.entity.Order;
import com.example.drools.service.DroolsContainerService;
import com.example.drools.service.DroolsRuleService;
import com.example.drools.service.DroolsRuleTestService;
import com.example.drools.service.ValidationResult;
import org.drools.core.base.RuleNameStartsWithAgendaFilter;
import org.kie.api.runtime.KieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

/**
 * Drools规则测试控制器
 * 专门处理规则测试相关的REST API
 *
 * @author System
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/rules/test")
public class DroolsRuleTestController {

    private static final Logger logger = LoggerFactory.getLogger(DroolsRuleTestController.class);

    @Autowired
    private DroolsRuleTestService droolsRuleTestService;

    @Autowired
    private DroolsRuleService droolsRuleService;
    @Autowired
    private DroolsContainerService containerService;

    @GetMapping("/demo")
    public ResponseEntity<ApiResponse<Void>> demo() {
        KieSession kieSession = containerService.createKieSession();
        //构造订单对象，设置原始价格，由规则引擎根据优惠规则计算优惠后的价格
        Order p = new Order();
        p.setOriginalPrice(10D);

        Order p2 = new Order();
        p2.setOriginalPrice(10D);

        //将数据提供给规则引擎，规则引擎会根据提供的数据进行规则匹配
        kieSession.insert(p);
        kieSession.insert(p2);

        //激活规则引擎，如果规则匹配成功则执行规则
//        new RuleNameEqualsAgendaFilter() 怎么根据这个快速定位到对应的rule ?
        // 规范话命名，例如使用规则引擎，要求rule_name 必须是 order开头的
        kieSession.fireAllRules(new RuleNameStartsWithAgendaFilter("order"));
        //关闭会话
        kieSession.dispose();

        logger.info("优惠前原始价格：{}，优惠后价格：{}", p.getOriginalPrice(), p.getRealPrice());
        return ResponseEntity.ok(ApiResponse.success());
    }

    /**
     * 执行规则测试
     *
     * @param request       测试请求
     * @param bindingResult 验证结果
     * @return 测试结果
     */
    @PostMapping
    public ResponseEntity<ApiResponse<TestResult>> executeTest(
            @RequestBody TestRuleRequest request,
            BindingResult bindingResult) {

        logger.info("接收到规则测试请求");

        try {

            // 验证测试请求的完整性
            if (!isValidTestRequest(request)) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.badRequest("必须提供规则ID"));
            }

            // 执行测试
            TestResult result = droolsRuleTestService.executeTest(request);

            String message = result.getSuccess() ? "规则测试执行成功" : "规则测试执行失败";
            logger.info("规则测试完成: {} - 触发规则数: {}, 执行时间: {}ms",
                    message, result.getFiredRulesCount(), result.getExecutionTime());

            return ResponseEntity.ok(ApiResponse.success(message, result));

        } catch (IllegalArgumentException e) {
            logger.warn("测试请求参数无效: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.badRequest("测试请求参数无效: " + e.getMessage()));
        } catch (Exception e) {
            logger.error("规则测试执行失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("规则测试执行失败: " + e.getMessage()));
        }
    }

    /**
     * 验证规则语法
     *
     * @param request 规则内容
     * @return 验证结果
     */
    @PostMapping("/validate")
    public ResponseEntity<ApiResponse<ValidationResult>> validateRuleSyntax(@RequestBody ValidateRuleRequest request) {

        logger.debug("接收到规则语法验证请求");

        try {
            if (!StringUtils.hasText(request.getRuleContent())) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.badRequest("规则内容不能为空"));
            }

            // 调用服务层验证规则
            ValidationResult result = droolsRuleService.validateRule(request.getRuleContent());

            String message = result.isValid() ? "规则语法验证通过" : "规则语法验证失败";
            logger.debug("规则语法验证完成: {}", message);

            return ResponseEntity.ok(ApiResponse.success(message, result));

        } catch (Exception e) {
            logger.error("规则语法验证失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("规则语法验证失败: " + e.getMessage()));
        }
    }

    // 私有辅助方法
    /**
     * 验证测试请求的有效性
     */
    private boolean isValidTestRequest(TestRuleRequest request) {
        return request.getRuleIds() != null && !request.getRuleIds().isEmpty();
    }
}