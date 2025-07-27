package com.example.drools.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.drools.dto.CreateRuleRequest;
import com.example.drools.dto.TestResult;
import com.example.drools.dto.TestRuleRequest;
import com.example.drools.dto.UpdateRuleRequest;
import com.example.drools.entity.DroolsRule;
import com.example.drools.exception.DroolsContainerException;
import com.example.drools.exception.RuleValidationException;
import com.example.drools.repository.DroolsRuleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Drools规则管理服务
 * 提供规则的CRUD操作和业务逻辑处理
 * 
 * @author System
 * @since 1.0.0
 */
@Service
@Transactional
public class DroolsRuleService {

    private static final Logger logger = LoggerFactory.getLogger(DroolsRuleService.class);

    @Autowired
    private DroolsRuleRepository droolsRuleRepository;

    @Autowired
    private DroolsContainerService droolsContainerService;

    @Autowired
    private DroolsRuleTestService droolsRuleTestService;

    /**
     * 创建新规则
     * 
     * @param request 创建规则请求
     * @return 创建的规则对象
     * @throws RuleValidationException 当规则验证失败时抛出
     * @throws DataIntegrityViolationException 当规则名称重复时抛出
     */
    public DroolsRule createRule(CreateRuleRequest request) {
        logger.info("开始创建规则: {}", request.getRuleName());

        try {
            // 验证请求参数
            validateCreateRuleRequest(request);

            // 检查规则名称是否已存在
            if (droolsRuleRepository.isRuleNameExists(request.getRuleName(), null)) {
                throw new DataIntegrityViolationException("规则名称已存在: " + request.getRuleName());
            }

            // 验证规则语法
            ValidationResult validationResult = droolsContainerService.validateRuleContent(request.getRuleContent());
            if (!validationResult.isValid()) {
                throw new RuleValidationException("规则语法验证失败: " + validationResult.getErrorMessage());
            }

            // 创建规则实体
            DroolsRule rule = new DroolsRule();
            rule.setRuleName(request.getRuleName());
            rule.setRuleContent(request.getRuleContent());
            rule.setDescription(request.getDescription());
            rule.setEnabled(request.getEnabled());
            rule.setVersion(request.getVersion());

            // 保存到数据库
            DroolsRule savedRule = droolsRuleRepository.save(rule);
            logger.info("规则已保存到数据库: {} (ID: {})", savedRule.getRuleName(), savedRule.getId());

            // 如果规则启用，添加到容器
            if (savedRule.getEnabled()) {
                try {
                    droolsContainerService.addRuleToContainer(savedRule);
                    logger.info("规则已添加到容器: {} (ID: {})", savedRule.getRuleName(), savedRule.getId());
                } catch (DroolsContainerException e) {
                    logger.error("添加规则到容器失败，回滚数据库操作: {} (ID: {})", 
                               savedRule.getRuleName(), savedRule.getId(), e);
                    // 回滚数据库操作
                    droolsRuleRepository.deleteById(savedRule.getId());
                    throw new DroolsContainerException("规则添加到容器失败，操作已回滚", e);
                }
            }

            logger.info("规则创建成功: {} (ID: {})", savedRule.getRuleName(), savedRule.getId());
            return savedRule;

        } catch (RuleValidationException | DataIntegrityViolationException | DroolsContainerException e) {
            throw e;
        } catch (Exception e) {
            logger.error("创建规则失败: {}", request.getRuleName(), e);
            throw new RuntimeException("创建规则失败: " + e.getMessage(), e);
        }
    }

    /**
     * 根据ID获取规则
     * 
     * @param id 规则ID
     * @return 规则对象，如果不存在则返回null
     */
    @Transactional(readOnly = true)
    public DroolsRule getRuleById(Long id) {
        logger.debug("查询规则: ID {}", id);
        
        if (id == null) {
            throw new IllegalArgumentException("规则ID不能为空");
        }
        
        return droolsRuleRepository.findById(id);
    }

    /**
     * 根据规则名称获取规则
     * 
     * @param ruleName 规则名称
     * @return 规则对象，如果不存在则返回null
     */
    @Transactional(readOnly = true)
    public DroolsRule getRuleByName(String ruleName) {
        logger.debug("查询规则: 名称 {}", ruleName);
        
        if (!StringUtils.hasText(ruleName)) {
            throw new IllegalArgumentException("规则名称不能为空");
        }
        
        return droolsRuleRepository.findByRuleName(ruleName);
    }

    /**
     * 获取所有规则
     * 
     * @return 所有规则列表
     */
    @Transactional(readOnly = true)
    public List<DroolsRule> getAllRules() {
        logger.debug("查询所有规则");
        return droolsRuleRepository.findAll();
    }

    /**
     * 获取所有启用的规则
     * 
     * @return 启用的规则列表
     */
    @Transactional(readOnly = true)
    public List<DroolsRule> getEnabledRules() {
        logger.debug("查询所有启用的规则");
        return droolsRuleRepository.findEnabledRules();
    }

    /**
     * 分页查询规则
     * 
     * @param pageNum 页码
     * @param pageSize 页面大小
     * @param enabled 是否启用（可选）
     * @param ruleName 规则名称关键字（可选）
     * @return 分页结果
     */
    @Transactional(readOnly = true)
    public IPage<DroolsRule> getRulesPage(int pageNum, int pageSize, Boolean enabled, String ruleName) {
        logger.debug("分页查询规则: 页码={}, 页面大小={}, 启用状态={}, 规则名称={}", 
                    pageNum, pageSize, enabled, ruleName);
        
        if (pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize < 1 || pageSize > 100) {
            pageSize = 10;
        }
        
        return droolsRuleRepository.findRulesPage(pageNum, pageSize, enabled, ruleName);
    }

    /**
     * 更新规则
     * 
     * @param id 规则ID
     * @param request 更新规则请求
     * @return 更新后的规则对象
     * @throws RuleValidationException 当规则验证失败时抛出
     * @throws DataIntegrityViolationException 当规则名称重复时抛出
     */
    public DroolsRule updateRule(Long id, UpdateRuleRequest request) {
        logger.info("开始更新规则: ID {} -> {}", id, request.getRuleName());

        try {
            // 验证请求参数
            validateUpdateRuleRequest(request);

            // 查找现有规则
            DroolsRule existingRule = droolsRuleRepository.findById(id);
            if (existingRule == null) {
                throw new IllegalArgumentException("规则不存在: ID " + id);
            }

            // 检查版本号（乐观锁）
            if (!existingRule.getVersion().equals(request.getVersion())) {
                throw new DataIntegrityViolationException("规则版本冲突，请刷新后重试");
            }

            // 检查规则名称是否已存在（排除当前规则）
            if (droolsRuleRepository.isRuleNameExists(request.getRuleName(), id)) {
                throw new DataIntegrityViolationException("规则名称已存在: " + request.getRuleName());
            }

            // 验证规则语法
            ValidationResult validationResult = droolsContainerService.validateRuleContent(request.getRuleContent());
            if (!validationResult.isValid()) {
                throw new RuleValidationException("规则语法验证失败: " + validationResult.getErrorMessage());
            }

            // 记录原始状态
            boolean wasEnabled = existingRule.getEnabled();
            boolean willBeEnabled = request.getEnabled();

            // 更新规则属性
            existingRule.setRuleName(request.getRuleName());
            existingRule.setRuleContent(request.getRuleContent());
            existingRule.setDescription(request.getDescription());
            existingRule.setEnabled(request.getEnabled());
            existingRule.setVersion(generateNextVersion(request.getVersion()));
            existingRule.setUpdateTime(LocalDateTime.now());

            // 保存到数据库
            DroolsRule updatedRule = droolsRuleRepository.save(existingRule);
            logger.info("规则已更新到数据库: {} (ID: {})", updatedRule.getRuleName(), updatedRule.getId());

            // 同步容器状态
            try {
                if (wasEnabled && willBeEnabled) {
                    // 规则保持启用状态，更新容器中的规则
                    droolsContainerService.updateRuleInContainer(updatedRule);
                    logger.info("容器中的规则已更新: {} (ID: {})", updatedRule.getRuleName(), updatedRule.getId());
                } else if (wasEnabled && !willBeEnabled) {
                    // 规则从启用变为禁用，从容器中移除
                    droolsContainerService.removeRuleFromContainer(updatedRule.getId());
                    logger.info("规则已从容器中移除: {} (ID: {})", updatedRule.getRuleName(), updatedRule.getId());
                } else if (!wasEnabled && willBeEnabled) {
                    // 规则从禁用变为启用，添加到容器
                    droolsContainerService.addRuleToContainer(updatedRule);
                    logger.info("规则已添加到容器: {} (ID: {})", updatedRule.getRuleName(), updatedRule.getId());
                }
                // 如果规则保持禁用状态，无需操作容器
            } catch (DroolsContainerException e) {
                logger.error("更新容器中的规则失败: {} (ID: {})", updatedRule.getRuleName(), updatedRule.getId(), e);
                throw new DroolsContainerException("规则容器同步失败", e);
            }

            logger.info("规则更新成功: {} (ID: {})", updatedRule.getRuleName(), updatedRule.getId());
            return updatedRule;

        } catch (RuleValidationException | DataIntegrityViolationException | DroolsContainerException e) {
            throw e;
        } catch (Exception e) {
            logger.error("更新规则失败: ID {}", id, e);
            throw new RuntimeException("更新规则失败: " + e.getMessage(), e);
        }
    }

    /**
     * 删除规则
     * 
     * @param id 规则ID
     * @throws DroolsContainerException 当从容器中移除规则失败时抛出
     */
    public void deleteRule(Long id) {
        logger.info("开始删除规则: ID {}", id);

        try {
            if (id == null) {
                throw new IllegalArgumentException("规则ID不能为空");
            }

            // 查找现有规则
            DroolsRule existingRule = droolsRuleRepository.findById(id);
            if (existingRule == null) {
                logger.warn("规则不存在，无需删除: ID {}", id);
                return;
            }

            // 如果规则启用，先从容器中移除
            if (existingRule.getEnabled()) {
                try {
                    droolsContainerService.removeRuleFromContainer(id);
                    logger.info("规则已从容器中移除: {} (ID: {})", existingRule.getRuleName(), id);
                } catch (DroolsContainerException e) {
                    logger.error("从容器中移除规则失败: {} (ID: {})", existingRule.getRuleName(), id, e);
                    throw new DroolsContainerException("从容器中移除规则失败", e);
                }
            }

            // 从数据库中删除（逻辑删除）
            boolean deleted = droolsRuleRepository.deleteById(id);
            if (deleted) {
                logger.info("规则删除成功: {} (ID: {})", existingRule.getRuleName(), id);
            } else {
                logger.warn("规则删除失败: ID {}", id);
                throw new RuntimeException("规则删除失败");
            }

        } catch (DroolsContainerException e) {
            throw e;
        } catch (Exception e) {
            logger.error("删除规则失败: ID {}", id, e);
            throw new RuntimeException("删除规则失败: " + e.getMessage(), e);
        }
    }

    /**
     * 批量删除规则
     * 
     * @param ids 规则ID列表
     * @return 删除的规则数量
     */
    public int batchDeleteRules(List<Long> ids) {
        logger.info("开始批量删除规则: {}", ids);

        if (ids == null || ids.isEmpty()) {
            return 0;
        }

        int deletedCount = 0;
        for (Long id : ids) {
            try {
                deleteRule(id);
                deletedCount++;
            } catch (Exception e) {
                logger.error("批量删除规则失败: ID {}", id, e);
                // 继续删除其他规则
            }
        }

        logger.info("批量删除规则完成: 成功删除 {} 条规则", deletedCount);
        return deletedCount;
    }

    /**
     * 更新规则状态
     * 
     * @param id 规则ID
     * @param enabled 是否启用
     * @return 更新后的规则对象
     */
    public DroolsRule updateRuleStatus(Long id, Boolean enabled) {
        logger.info("开始更新规则状态: ID {} -> {}", id, enabled ? "启用" : "禁用");

        try {
            if (id == null || enabled == null) {
                throw new IllegalArgumentException("规则ID和启用状态不能为空");
            }

            // 查找现有规则
            DroolsRule existingRule = droolsRuleRepository.findById(id);
            if (existingRule == null) {
                throw new IllegalArgumentException("规则不存在: ID " + id);
            }

            // 如果状态没有变化，直接返回
            if (existingRule.getEnabled().equals(enabled)) {
                logger.debug("规则状态无变化: {} (ID: {})", existingRule.getRuleName(), id);
                return existingRule;
            }

            // 更新数据库状态
            boolean updated = droolsRuleRepository.updateRuleStatus(id, enabled);
            if (!updated) {
                throw new RuntimeException("更新规则状态失败");
            }

            // 重新查询更新后的规则
            DroolsRule updatedRule = droolsRuleRepository.findById(id);

            // 同步容器状态
            try {
                if (enabled) {
                    // 启用规则，添加到容器
                    droolsContainerService.addRuleToContainer(updatedRule);
                    logger.info("规则已添加到容器: {} (ID: {})", updatedRule.getRuleName(), id);
                } else {
                    // 禁用规则，从容器中移除
                    droolsContainerService.removeRuleFromContainer(id);
                    logger.info("规则已从容器中移除: {} (ID: {})", updatedRule.getRuleName(), id);
                }
            } catch (DroolsContainerException e) {
                logger.error("同步容器状态失败: {} (ID: {})", updatedRule.getRuleName(), id, e);
                // 回滚数据库状态
                droolsRuleRepository.updateRuleStatus(id, !enabled);
                throw new DroolsContainerException("规则容器同步失败，操作已回滚", e);
            }

            logger.info("规则状态更新成功: {} (ID: {}) -> {}", 
                       updatedRule.getRuleName(), id, enabled ? "启用" : "禁用");
            return updatedRule;

        } catch (DroolsContainerException e) {
            throw e;
        } catch (Exception e) {
            logger.error("更新规则状态失败: ID {}", id, e);
            throw new RuntimeException("更新规则状态失败: " + e.getMessage(), e);
        }
    }

    /**
     * 验证规则内容
     * 
     * @param ruleContent 规则内容
     * @return 验证结果
     */
    @Transactional(readOnly = true)
    public ValidationResult validateRule(String ruleContent) {
        logger.debug("验证规则内容");
        
        if (!StringUtils.hasText(ruleContent)) {
            return new ValidationResult(false, "规则内容不能为空");
        }
        
        return droolsContainerService.validateRuleContent(ruleContent);
    }

    /**
     * 测试规则执行
     * 
     * @param request 测试请求
     * @return 测试结果
     */
    @Transactional(readOnly = true)
    public TestResult executeTest(TestRuleRequest request) {
        logger.info("开始执行规则测试");
        
        try {
            validateTestRuleRequest(request);
            return droolsRuleTestService.executeTest(request);
        } catch (Exception e) {
            logger.error("规则测试执行失败", e);
            return new TestResult(e.getMessage(), getStackTrace(e));
        }
    }

    /**
     * 搜索规则
     * 
     * @param keyword 关键字
     * @return 匹配的规则列表
     */
    @Transactional(readOnly = true)
    public List<DroolsRule> searchRules(String keyword) {
        logger.debug("搜索规则: 关键字 {}", keyword);
        
        if (!StringUtils.hasText(keyword)) {
            return getAllRules();
        }
        
        return droolsRuleRepository.searchRules(keyword);
    }

    /**
     * 获取规则统计信息
     * 
     * @return 统计信息
     */
    @Transactional(readOnly = true)
    public RuleStatistics getRuleStatistics() {
        logger.debug("获取规则统计信息");
        
        RuleStatistics statistics = new RuleStatistics();
        statistics.setTotalRules(droolsRuleRepository.countRules());
        statistics.setEnabledRules(droolsRuleRepository.countEnabledRules());
        statistics.setDisabledRules(statistics.getTotalRules() - statistics.getEnabledRules());
        statistics.setLoadedRules((long) droolsContainerService.getLoadedRulesCount());
        
        return statistics;
    }

    // 私有辅助方法

    /**
     * 验证创建规则请求
     */
    private void validateCreateRuleRequest(CreateRuleRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("创建规则请求不能为空");
        }
        if (!StringUtils.hasText(request.getRuleName())) {
            throw new IllegalArgumentException("规则名称不能为空");
        }
        if (!StringUtils.hasText(request.getRuleContent())) {
            throw new IllegalArgumentException("规则内容不能为空");
        }
        if (request.getEnabled() == null) {
            throw new IllegalArgumentException("启用状态不能为空");
        }
    }

    /**
     * 验证更新规则请求
     */
    private void validateUpdateRuleRequest(UpdateRuleRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("更新规则请求不能为空");
        }
        if (!StringUtils.hasText(request.getRuleName())) {
            throw new IllegalArgumentException("规则名称不能为空");
        }
        if (!StringUtils.hasText(request.getRuleContent())) {
            throw new IllegalArgumentException("规则内容不能为空");
        }
        if (request.getEnabled() == null) {
            throw new IllegalArgumentException("启用状态不能为空");
        }
        if (!StringUtils.hasText(request.getVersion())) {
            throw new IllegalArgumentException("版本号不能为空");
        }
    }

    /**
     * 验证测试规则请求
     */
    private void validateTestRuleRequest(TestRuleRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("测试规则请求不能为空");
        }

        if ((request.getRuleIds() == null || request.getRuleIds().isEmpty())) {
            throw new IllegalArgumentException("必须提供规则ID列表");
        }
    }

    /**
     * 生成下一个版本号
     */
    private String generateNextVersion(String currentVersion) {
        if (!StringUtils.hasText(currentVersion)) {
            return "1.0";
        }
        
        try {
            String[] parts = currentVersion.split("\\.");
            if (parts.length >= 2) {
                int major = Integer.parseInt(parts[0]);
                int minor = Integer.parseInt(parts[1]);
                return major + "." + (minor + 1);
            }
        } catch (NumberFormatException e) {
            logger.warn("无法解析版本号: {}", currentVersion);
        }
        
        return currentVersion + ".1";
    }

    /**
     * 获取异常堆栈跟踪
     */
    private String getStackTrace(Exception e) {
        java.io.StringWriter sw = new java.io.StringWriter();
        java.io.PrintWriter pw = new java.io.PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }

    /**
     * 规则统计信息内部类
     */
    public static class RuleStatistics {
        private Long totalRules;
        private Long enabledRules;
        private Long disabledRules;
        private Long loadedRules;

        // Getter and Setter methods
        public Long getTotalRules() {
            return totalRules;
        }

        public void setTotalRules(Long totalRules) {
            this.totalRules = totalRules;
        }

        public Long getEnabledRules() {
            return enabledRules;
        }

        public void setEnabledRules(Long enabledRules) {
            this.enabledRules = enabledRules;
        }

        public Long getDisabledRules() {
            return disabledRules;
        }

        public void setDisabledRules(Long disabledRules) {
            this.disabledRules = disabledRules;
        }

        public Long getLoadedRules() {
            return loadedRules;
        }

        public void setLoadedRules(Long loadedRules) {
            this.loadedRules = loadedRules;
        }

        @Override
        public String toString() {
            return "RuleStatistics{" +
                    "totalRules=" + totalRules +
                    ", enabledRules=" + enabledRules +
                    ", disabledRules=" + disabledRules +
                    ", loadedRules=" + loadedRules +
                    '}';
        }
    }
}