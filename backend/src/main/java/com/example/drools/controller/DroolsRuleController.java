package com.example.drools.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.drools.dto.ApiResponse;
import com.example.drools.dto.CreateRuleRequest;
import com.example.drools.dto.UpdateRuleRequest;
import com.example.drools.entity.DroolsRule;
import com.example.drools.exception.DroolsContainerException;
import com.example.drools.exception.RuleValidationException;
import com.example.drools.service.DroolsRuleService;
import com.example.drools.service.ValidationResult;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Drools规则管理控制器
 * 提供规则的CRUD操作和测试功能的REST API
 * 
 * @author System
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/rules")
public class DroolsRuleController {

    private static final Logger logger = LoggerFactory.getLogger(DroolsRuleController.class);

    @Autowired
    private DroolsRuleService droolsRuleService;

    /**
     * 创建新规则
     * 
     * @param request 创建规则请求
     * @param bindingResult 验证结果
     * @return 创建的规则对象
     */
    @PostMapping
    public ResponseEntity<ApiResponse<DroolsRule>> createRule(
            @Valid @RequestBody CreateRuleRequest request,
            BindingResult bindingResult) {
        
        logger.info("接收到创建规则请求: {}", request.getRuleName());

        try {
            // 检查请求验证结果
            if (bindingResult.hasErrors()) {
                String errorMessage = bindingResult.getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .collect(Collectors.joining(", "));
                logger.warn("创建规则请求验证失败: {}", errorMessage);
                return ResponseEntity.badRequest()
                    .body(ApiResponse.badRequest("请求参数验证失败: " + errorMessage));
            }

            // 调用服务层创建规则
            DroolsRule createdRule = droolsRuleService.createRule(request);
            
            logger.info("规则创建成功: {} (ID: {})", createdRule.getRuleName(), createdRule.getId());
            return ResponseEntity.ok(ApiResponse.success("规则创建成功", createdRule));

        } catch (RuleValidationException e) {
            logger.warn("规则语法验证失败: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(ApiResponse.unprocessableEntity("规则语法验证失败: " + e.getMessage()));
        } catch (DataIntegrityViolationException e) {
            logger.warn("规则名称冲突: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.conflict("规则名称已存在，请使用其他名称"));
        } catch (DroolsContainerException e) {
            logger.error("规则容器操作失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("规则容器操作失败: " + e.getMessage()));
        } catch (Exception e) {
            logger.error("创建规则失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("创建规则失败: " + e.getMessage()));
        }
    }

    /**
     * 根据ID获取规则
     * 
     * @param id 规则ID
     * @return 规则对象
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DroolsRule>> getRuleById(
            @PathVariable Long id) {
        
        logger.debug("接收到获取规则请求: ID {}", id);

        try {
            if (id == null || id <= 0) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.badRequest("规则ID必须是正整数"));
            }

            DroolsRule rule = droolsRuleService.getRuleById(id);
            if (rule == null) {
                logger.warn("规则不存在: ID {}", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.notFound("规则不存在: ID " + id));
            }

            logger.debug("规则获取成功: {} (ID: {})", rule.getRuleName(), id);
            return ResponseEntity.ok(ApiResponse.success(rule));

        } catch (Exception e) {
            logger.error("获取规则失败: ID {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("获取规则失败: " + e.getMessage()));
        }
    }

    /**
     * 获取所有规则
     * 
     * @return 所有规则列表
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<DroolsRule>>> getAllRules(
            @RequestParam(required = false) Boolean enabled,
            @RequestParam(required = false) String keyword) {
        
        logger.debug("接收到获取规则列表请求: enabled={}, keyword={}", enabled, keyword);

        try {
            List<DroolsRule> rules;
            
            if (StringUtils.hasText(keyword)) {
                // 根据关键字搜索
                rules = droolsRuleService.searchRules(keyword);
                if (enabled != null) {
                    // 进一步按启用状态筛选
                    rules = rules.stream()
                        .filter(rule -> enabled.equals(rule.getEnabled()))
                        .collect(Collectors.toList());
                }
            } else if (enabled != null) {
                // 按启用状态筛选
                if (enabled) {
                    rules = droolsRuleService.getEnabledRules();
                } else {
                    rules = droolsRuleService.getAllRules().stream()
                        .filter(rule -> !rule.getEnabled())
                        .collect(Collectors.toList());
                }
            } else {
                // 获取所有规则
                rules = droolsRuleService.getAllRules();
            }

            logger.debug("规则列表获取成功: 共 {} 条规则", rules.size());
            return ResponseEntity.ok(ApiResponse.success(rules));

        } catch (Exception e) {
            logger.error("获取规则列表失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("获取规则列表失败: " + e.getMessage()));
        }
    }

    /**
     * 分页获取规则
     * 
     * @param pageNum 页码
     * @param pageSize 页面大小
     * @param enabled 是否启用
     * @param ruleName 规则名称关键字
     * @return 分页结果
     */
    @GetMapping("/page")
    public ResponseEntity<ApiResponse<IPage<DroolsRule>>> getRulesPage(

            @RequestParam(defaultValue = "1") int pageNum,

            @RequestParam(defaultValue = "10") int pageSize,

            @RequestParam(required = false) Boolean enabled,
            @RequestParam(required = false) String ruleName) {
        
        logger.debug("接收到分页获取规则请求: pageNum={}, pageSize={}, enabled={}, ruleName={}", 
                    pageNum, pageSize, enabled, ruleName);

        try {
            IPage<DroolsRule> page = droolsRuleService.getRulesPage(pageNum, pageSize, enabled, ruleName);
            
            logger.debug("分页规则获取成功: 第{}页，共{}条记录", pageNum, page.getTotal());
            return ResponseEntity.ok(ApiResponse.success(page));

        } catch (Exception e) {
            logger.error("分页获取规则失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("分页获取规则失败: " + e.getMessage()));
        }
    }

    /**
     * 更新规则
     * 
     * @param id 规则ID
     * @param request 更新规则请求
     * @param bindingResult 验证结果
     * @return 更新后的规则对象
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DroolsRule>> updateRule(
            @PathVariable Long id,
            @Valid @RequestBody UpdateRuleRequest request,
            BindingResult bindingResult) {
        
        logger.info("接收到更新规则请求: ID {} -> {}", id, request.getRuleName());

        try {
            if (id == null || id <= 0) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.badRequest("规则ID必须是正整数"));
            }

            // 检查请求验证结果
            if (bindingResult.hasErrors()) {
                String errorMessage = bindingResult.getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .collect(Collectors.joining(", "));
                logger.warn("更新规则请求验证失败: {}", errorMessage);
                return ResponseEntity.badRequest()
                    .body(ApiResponse.badRequest("请求参数验证失败: " + errorMessage));
            }

            // 调用服务层更新规则
            DroolsRule updatedRule = droolsRuleService.updateRule(id, request);
            
            logger.info("规则更新成功: {} (ID: {})", updatedRule.getRuleName(), updatedRule.getId());
            return ResponseEntity.ok(ApiResponse.success("规则更新成功", updatedRule));

        } catch (IllegalArgumentException e) {
            logger.warn("规则不存在或参数无效: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.notFound(e.getMessage()));
        } catch (RuleValidationException e) {
            logger.warn("规则语法验证失败: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(ApiResponse.unprocessableEntity("规则语法验证失败: " + e.getMessage()));
        } catch (DataIntegrityViolationException e) {
            logger.warn("规则更新冲突: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.conflict(e.getMessage()));
        } catch (DroolsContainerException e) {
            logger.error("规则容器操作失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("规则容器操作失败: " + e.getMessage()));
        } catch (Exception e) {
            logger.error("更新规则失败: ID {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("更新规则失败: " + e.getMessage()));
        }
    }

    /**
     * 删除规则
     * 
     * @param id 规则ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteRule(
            @PathVariable Long id) {
        
        logger.info("接收到删除规则请求: ID {}", id);

        try {
            if (id == null || id <= 0) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.badRequest("规则ID必须是正整数"));
            }

            // 调用服务层删除规则
            droolsRuleService.deleteRule(id);
            
            logger.info("规则删除成功: ID {}", id);
            return ResponseEntity.ok(ApiResponse.success("规则删除成功"));

        } catch (DroolsContainerException e) {
            logger.error("规则容器操作失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("规则容器操作失败: " + e.getMessage()));
        } catch (Exception e) {
            logger.error("删除规则失败: ID {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("删除规则失败: " + e.getMessage()));
        }
    }

    /**
     * 批量删除规则
     * 
     * @param ids 规则ID列表
     * @return 删除结果
     */
    @DeleteMapping("/batch")
    public ResponseEntity<ApiResponse<String>> batchDeleteRules(
            @RequestBody List<Long> ids) {
        
        logger.info("接收到批量删除规则请求: {}", ids);

        try {
            if (ids == null || ids.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.badRequest("规则ID列表不能为空"));
            }

            // 验证ID格式
            for (Long id : ids) {
                if (id == null || id <= 0) {
                    return ResponseEntity.badRequest()
                        .body(ApiResponse.badRequest("规则ID必须是正整数"));
                }
            }

            // 调用服务层批量删除规则
            int deletedCount = droolsRuleService.batchDeleteRules(ids);
            
            String message = String.format("批量删除完成，成功删除 %d 条规则", deletedCount);
            logger.info(message);
            return ResponseEntity.ok(ApiResponse.success(message, message));

        } catch (Exception e) {
            logger.error("批量删除规则失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("批量删除规则失败: " + e.getMessage()));
        }
    }

    /**
     * 更新规则状态
     * 
     * @param id 规则ID
     * @param enabled 是否启用
     * @return 更新后的规则对象
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<DroolsRule>> updateRuleStatus(

            @PathVariable Long id,
            @RequestParam Boolean enabled) {
        
        logger.info("接收到更新规则状态请求: ID {} -> {}", id, enabled ? "启用" : "禁用");

        try {
            if (id == null || id <= 0) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.badRequest("规则ID必须是正整数"));
            }

            if (enabled == null) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.badRequest("启用状态不能为空"));
            }

            // 调用服务层更新规则状态
            DroolsRule updatedRule = droolsRuleService.updateRuleStatus(id, enabled);
            
            String message = String.format("规则状态更新成功: %s", enabled ? "已启用" : "已禁用");
            logger.info("规则状态更新成功: {} (ID: {}) -> {}", 
                       updatedRule.getRuleName(), id, enabled ? "启用" : "禁用");
            return ResponseEntity.ok(ApiResponse.success(message, updatedRule));

        } catch (IllegalArgumentException e) {
            logger.warn("规则不存在或参数无效: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.notFound(e.getMessage()));
        } catch (DroolsContainerException e) {
            logger.error("规则容器操作失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("规则容器操作失败: " + e.getMessage()));
        } catch (Exception e) {
            logger.error("更新规则状态失败: ID {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("更新规则状态失败: " + e.getMessage()));
        }
    }

    /**
     * 验证规则语法
     * 
     * @param ruleContent 规则内容
     * @return 验证结果
     */
    @PostMapping("/validate")
    public ResponseEntity<ApiResponse<ValidationResult>> validateRule(
            @RequestBody String ruleContent) {
        
        logger.debug("接收到规则验证请求");

        try {
            if (!StringUtils.hasText(ruleContent)) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.badRequest("规则内容不能为空"));
            }

            // 调用服务层验证规则
            ValidationResult result = droolsRuleService.validateRule(ruleContent);
            
            String message = result.isValid() ? "规则语法验证通过" : "规则语法验证失败";
            logger.debug("规则验证完成: {}", message);
            return ResponseEntity.ok(ApiResponse.success(message, result));

        } catch (Exception e) {
            logger.error("规则验证失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("规则验证失败: " + e.getMessage()));
        }
    }

    /**
     * 搜索规则
     * 
     * @param keyword 搜索关键字
     * @return 匹配的规则列表
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<DroolsRule>>> searchRules(
            @RequestParam String keyword) {
        
        logger.debug("接收到搜索规则请求: 关键字 {}", keyword);

        try {
            List<DroolsRule> rules = droolsRuleService.searchRules(keyword);
            
            logger.debug("规则搜索完成: 找到 {} 条匹配规则", rules.size());
            return ResponseEntity.ok(ApiResponse.success(rules));

        } catch (Exception e) {
            logger.error("搜索规则失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("搜索规则失败: " + e.getMessage()));
        }
    }

    /**
     * 获取规则统计信息
     * 
     * @return 统计信息
     */
    @GetMapping("/statistics")
    public ResponseEntity<ApiResponse<DroolsRuleService.RuleStatistics>> getRuleStatistics() {
        
        logger.debug("接收到获取规则统计请求");

        try {
            DroolsRuleService.RuleStatistics statistics = droolsRuleService.getRuleStatistics();
            
            logger.debug("规则统计获取成功: {}", statistics);
            return ResponseEntity.ok(ApiResponse.success(statistics));

        } catch (Exception e) {
            logger.error("获取规则统计失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("获取规则统计失败: " + e.getMessage()));
        }
    }


}