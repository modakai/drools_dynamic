package com.example.drools.service;

import com.example.drools.entity.DroolsRule;
import com.example.drools.exception.DroolsContainerException;
import com.example.drools.repository.DroolsRuleRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.kie.api.KieServices;
import org.kie.api.builder.*;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Drools容器管理服务
 * 负责管理Drools规则引擎容器的生命周期，包括初始化、规则加载和容器管理
 * 
 * @author System
 * @since 1.0.0
 */
@Service
public class DroolsContainerService {

    private static final Logger logger = LoggerFactory.getLogger(DroolsContainerService.class);
    
    private static final String DEFAULT_PACKAGE = "com.example.rules";
    private static final String RULES_PATH = "src/main/resources/rules/";
    
    @Autowired
    private DroolsRuleRepository droolsRuleRepository;
    
    private KieServices kieServices;
    private KieContainer kieContainer;
    private KieFileSystem kieFileSystem;
    
    // 用于存储规则ID到文件路径的映射
    private final ConcurrentHashMap<Long, String> rulePathMap = new ConcurrentHashMap<>();
    
    // 读写锁，保证容器操作的线程安全
    private final ReentrantReadWriteLock containerLock = new ReentrantReadWriteLock();
    private final ReentrantReadWriteLock.ReadLock readLock = containerLock.readLock();
    private final ReentrantReadWriteLock.WriteLock writeLock = containerLock.writeLock();

    /**
     * 服务初始化方法
     * 在Spring容器启动后自动调用，初始化Drools容器并加载现有规则
     */
    @PostConstruct
    public void initializeContainer() {
        logger.info("开始初始化Drools容器服务...");
        
        try {
            // 初始化KieServices
            kieServices = KieServices.Factory.get();
            kieFileSystem = kieServices.newKieFileSystem();
            
            // 加载数据库中的所有启用规则
            loadRulesFromDatabase();
            
            // 构建并创建容器
            buildContainer();
            
            logger.info("Drools容器服务初始化完成，共加载 {} 条规则", rulePathMap.size());
            
        } catch (Exception e) {
            logger.error("Drools容器服务初始化失败", e);
            throw new RuntimeException("Failed to initialize Drools container", e);
        }
    }

    /**
     * 从数据库加载所有启用的规则到容器中
     */
    private void loadRulesFromDatabase() {
        logger.info("开始从数据库加载规则...");
        
        try {
            List<DroolsRule> enabledRules = droolsRuleRepository.findEnabledRules();
            logger.info("从数据库查询到 {} 条启用的规则", enabledRules.size());
            
            for (DroolsRule rule : enabledRules) {
                addRuleToFileSystem(rule);
            }
            
            logger.info("规则加载完成，共加载 {} 条规则", enabledRules.size());
            
        } catch (Exception e) {
            logger.error("从数据库加载规则失败", e);
            throw new RuntimeException("Failed to load rules from database", e);
        }
    }

    /**
     * 将规则添加到KieFileSystem中
     * 
     * @param rule 规则对象
     */
    private void addRuleToFileSystem(DroolsRule rule) {
        if (rule == null || rule.getId() == null || rule.getRuleContent() == null) {
            logger.warn("规则对象无效，跳过加载: {}", rule);
            return;
        }
        
        try {
            String rulePath = generateRulePath(rule);
            
            kieFileSystem.write(rulePath, rule.getRuleContent());
            rulePathMap.put(rule.getId(), rulePath);
            
            logger.debug("规则已添加到文件系统: {} -> {}", rule.getRuleName(), rulePath);
            
        } catch (Exception e) {
            logger.error("添加规则到文件系统失败: {}", rule.getRuleName(), e);
            throw new RuntimeException("Failed to add rule to file system: " + rule.getRuleName(), e);
        }
    }

    /**
     * 生成规则文件路径
     * 
     * @param rule 规则对象
     * @return 规则文件路径
     */
    private String generateRulePath(DroolsRule rule) {
        // rule 里面有PACKAGE,那么是不是我看可以创建一个
        return RULES_PATH + rule.getRuleName() + ".drl";
    }

    /**
     * 包装规则内容，添加package声明和必要的import语句
     * 
     * @param rule 规则对象
     * @return 完整的规则内容
     */
    private String wrapRuleContent(DroolsRule rule) {
        StringBuilder sb = new StringBuilder();
        
        // 添加package声明
        sb.append("package ").append(DEFAULT_PACKAGE).append(";\n\n");
        
        // 添加常用的import语句
        sb.append("import java.util.*;\n");
        sb.append("import java.math.*;\n");
        sb.append("import java.time.*;\n\n");
        
        // 添加规则内容注释
        sb.append("// 规则ID: ").append(rule.getId()).append("\n");
        sb.append("// 规则名称: ").append(rule.getRuleName()).append("\n");
        if (rule.getDescription() != null && !rule.getDescription().trim().isEmpty()) {
            sb.append("// 规则描述: ").append(rule.getDescription()).append("\n");
        }
        sb.append("// 创建时间: ").append(rule.getCreateTime()).append("\n");
        sb.append("// 更新时间: ").append(rule.getUpdateTime()).append("\n\n");
        
        // 添加实际的规则内容
        sb.append(rule.getRuleContent());
        
        return sb.toString();
    }

    /**
     * 构建KieContainer
     */
    private void buildContainer() {
        logger.info("开始构建Drools容器...");
        
        try {
            writeLock.lock();
            
            KieBuilder kieBuilder = kieServices.newKieBuilder(kieFileSystem);
            kieBuilder.buildAll();
            
            Results results = kieBuilder.getResults();
            if (results.hasMessages(Message.Level.ERROR)) {
                logger.error("构建Drools容器时发现错误:");
                for (Message message : results.getMessages(Message.Level.ERROR)) {
                    logger.error("  - {}", message.getText());
                }
                throw new RuntimeException("Failed to build Drools container due to compilation errors");
            }
            
            if (results.hasMessages(Message.Level.WARNING)) {
                logger.warn("构建Drools容器时发现警告:");
                for (Message message : results.getMessages(Message.Level.WARNING)) {
                    logger.warn("  - {}", message.getText());
                }
            }
            
            KieModule kieModule = kieBuilder.getKieModule();
            kieContainer = kieServices.newKieContainer(kieModule.getReleaseId());
            
            logger.info("Drools容器构建成功");
            
        } catch (Exception e) {
            logger.error("构建Drools容器失败", e);
            throw new RuntimeException("Failed to build Drools container", e);
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * 获取KieSession实例
     * 
     * @return KieSession实例
     */
    public KieSession createKieSession() {
        readLock.lock();
        try {
            if (kieContainer == null) {
                throw new IllegalStateException("Drools container is not initialized");
            }
            return kieContainer.newKieSession();
        } finally {
            readLock.unlock();
        }
    }

    /**
     * 获取KieContainer实例
     * 
     * @return KieContainer实例
     */
    public KieContainer getKieContainer() {
        readLock.lock();
        try {
            return kieContainer;
        } finally {
            readLock.unlock();
        }
    }

    /**
     * 检查容器是否已初始化
     * 
     * @return 是否已初始化
     */
    public boolean isContainerInitialized() {
        readLock.lock();
        try {
            return kieContainer != null;
        } finally {
            readLock.unlock();
        }
    }

    /**
     * 获取当前加载的规则数量
     * 
     * @return 规则数量
     */
    public int getLoadedRulesCount() {
        return rulePathMap.size();
    }

    /**
     * 检查指定规则是否已加载到容器中
     * 
     * @param ruleId 规则ID
     * @return 是否已加载
     */
    public boolean isRuleLoaded(Long ruleId) {
        return rulePathMap.containsKey(ruleId);
    }

    /**
     * 获取所有已加载规则的ID列表
     * 
     * @return 规则ID列表
     */
    public List<Long> getLoadedRuleIds() {
        return List.copyOf(rulePathMap.keySet());
    }

    /**
     * 重新构建容器
     * 当规则发生变化时调用此方法重新构建容器
     */
    public void rebuildContainer() {
        logger.info("开始重新构建Drools容器...");
        
        try {
            writeLock.lock();
            
            // 清理现有的文件系统
            kieFileSystem = kieServices.newKieFileSystem();
            rulePathMap.clear();
            
            // 重新加载规则
            loadRulesFromDatabase();
            
            // 重新构建容器
            buildContainer();
            
            logger.info("Drools容器重新构建完成");
            
        } catch (Exception e) {
            logger.error("重新构建Drools容器失败", e);
            throw new RuntimeException("Failed to rebuild Drools container", e);
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * 添加规则到容器
     * 
     * @param rule 规则对象
     * @throws DroolsContainerException 当添加规则失败时抛出
     */
    public void addRuleToContainer(DroolsRule rule) {
        if (rule == null || rule.getId() == null) {
            throw new IllegalArgumentException("Rule or rule ID cannot be null");
        }
        
        logger.info("开始添加规则到容器: {} (ID: {})", rule.getRuleName(), rule.getId());
        
        try {
            writeLock.lock();
            
            // 检查规则是否已存在
            if (rulePathMap.containsKey(rule.getId())) {
                logger.warn("规则已存在于容器中，将进行更新: {} (ID: {})", rule.getRuleName(), rule.getId());
                updateRuleInContainer(rule);
                return;
            }
            
            // 添加规则到文件系统
            addRuleToFileSystem(rule);
            
            // 重新构建容器
            buildContainer();
            
            logger.info("规则成功添加到容器: {} (ID: {})", rule.getRuleName(), rule.getId());
            
        } catch (DroolsContainerException e) {
            throw e;
        } catch (Exception e) {
            logger.error("添加规则到容器失败: {} (ID: {})", rule.getRuleName(), rule.getId(), e);
            throw new DroolsContainerException(
                "Failed to add rule to container: " + rule.getRuleName(), 
                e, "ADD_RULE", rule.getId());
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * 更新容器中的规则
     * 
     * @param rule 更新后的规则对象
     * @throws DroolsContainerException 当更新规则失败时抛出
     */
    public void updateRuleInContainer(DroolsRule rule) {
        if (rule == null || rule.getId() == null) {
            throw new IllegalArgumentException("Rule or rule ID cannot be null");
        }
        
        logger.info("开始更新容器中的规则: {} (ID: {})", rule.getRuleName(), rule.getId());
        
        try {
            writeLock.lock();
            
            // 验证规则语法
            ValidationResult validationResult = validateRuleContent(rule.getRuleContent());
            if (!validationResult.isValid()) {
                throw new DroolsContainerException(
                    "Rule validation failed: " + validationResult.getErrorMessage(),
                    "UPDATE_RULE", rule.getId());
            }
            
            // 检查规则是否存在
            if (!rulePathMap.containsKey(rule.getId())) {
                logger.warn("规则不存在于容器中，将进行添加: {} (ID: {})", rule.getRuleName(), rule.getId());
                addRuleToContainer(rule);
                return;
            }
            
            // 移除旧规则
            String oldRulePath = rulePathMap.get(rule.getId());
            kieFileSystem.delete(oldRulePath);
            
            // 添加新规则
            addRuleToFileSystem(rule);
            
            // 重新构建容器
            buildContainer();
            
            logger.info("规则成功更新: {} (ID: {})", rule.getRuleName(), rule.getId());
            
        } catch (DroolsContainerException e) {
            throw e;
        } catch (Exception e) {
            logger.error("更新容器中的规则失败: {} (ID: {})", rule.getRuleName(), rule.getId(), e);
            throw new DroolsContainerException(
                "Failed to update rule in container: " + rule.getRuleName(), 
                e, "UPDATE_RULE", rule.getId());
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * 从容器中移除规则
     * 
     * @param ruleId 规则ID
     * @throws DroolsContainerException 当移除规则失败时抛出
     */
    public void removeRuleFromContainer(Long ruleId) {
        if (ruleId == null) {
            throw new IllegalArgumentException("Rule ID cannot be null");
        }
        
        logger.info("开始从容器中移除规则: ID {}", ruleId);
        
        try {
            writeLock.lock();
            
            // 检查规则是否存在
            if (!rulePathMap.containsKey(ruleId)) {
                logger.warn("规则不存在于容器中，无需移除: ID {}", ruleId);
                return;
            }
            
            // 从文件系统中删除规则
            String rulePath = rulePathMap.get(ruleId);
            kieFileSystem.delete(rulePath);
            rulePathMap.remove(ruleId);
            
            // 重新构建容器
            buildContainer();
            
            logger.info("规则成功从容器中移除: ID {}", ruleId);
            
        } catch (Exception e) {
            logger.error("从容器中移除规则失败: ID {}", ruleId, e);
            throw new DroolsContainerException(
                "Failed to remove rule from container", 
                e, "REMOVE_RULE", ruleId);
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * 验证规则内容的语法
     * 
     * @param ruleContent 规则内容
     * @return 验证结果
     */
    public ValidationResult validateRuleContent(String ruleContent) {
        if (ruleContent == null || ruleContent.trim().isEmpty()) {
            return new ValidationResult(false, "Rule content cannot be empty");
        }
        
        try {
            // 创建临时的KieFileSystem进行语法验证
            KieServices tempKieServices = kieServices;
            KieFileSystem tempKieFileSystem = tempKieServices.newKieFileSystem();
            
            // 包装规则内容
//            String wrappedContent = wrapRuleContentForValidation(ruleContent);
            String wrappedContent = ruleContent;
            String tempRulePath = "src/main/resources/temp/validation_rule.drl";
            
            tempKieFileSystem.write(tempRulePath, wrappedContent);
            
            // 构建并检查错误
            KieBuilder tempKieBuilder = tempKieServices.newKieBuilder(tempKieFileSystem);
            tempKieBuilder.buildAll();
            
            Results results = tempKieBuilder.getResults();
            if (results.hasMessages(Message.Level.ERROR)) {
                StringBuilder errorMsg = new StringBuilder("Rule compilation errors:\n");
                for (Message message : results.getMessages(Message.Level.ERROR)) {
                    errorMsg.append("- ").append(message.getText()).append("\n");
                }
                return new ValidationResult(false, errorMsg.toString());
            }
            
            // 检查警告
            if (results.hasMessages(Message.Level.WARNING)) {
                StringBuilder warningMsg = new StringBuilder("Rule compilation warnings:\n");
                for (Message message : results.getMessages(Message.Level.WARNING)) {
                    warningMsg.append("- ").append(message.getText()).append("\n");
                }
                return new ValidationResult(true, null, warningMsg.toString());
            }
            
            return new ValidationResult(true, null);
            
        } catch (Exception e) {
            logger.error("规则语法验证失败", e);
            return new ValidationResult(false, "Validation failed: " + e.getMessage());
        }
    }


    /**
     * 同步容器与数据库
     * 确保容器中的规则与数据库中的启用规则保持一致
     */
    public void syncContainerWithDatabase() {
        logger.info("开始同步容器与数据库...");
        
        try {
            writeLock.lock();
            
            // 获取数据库中的所有启用规则
            List<DroolsRule> enabledRules = droolsRuleRepository.findEnabledRules();
            
            // 获取当前容器中的规则ID
            List<Long> currentRuleIds = getLoadedRuleIds();
            
            // 找出需要添加的规则
            List<DroolsRule> rulesToAdd = enabledRules.stream()
                .filter(rule -> !currentRuleIds.contains(rule.getId()))
                .toList();
            
            // 找出需要移除的规则
            List<Long> ruleIdsToRemove = currentRuleIds.stream()
                .filter(ruleId -> enabledRules.stream().noneMatch(rule -> rule.getId().equals(ruleId)))
                .toList();
            
            // 找出需要更新的规则（通过比较更新时间）
            List<DroolsRule> rulesToUpdate = enabledRules.stream()
                .filter(rule -> currentRuleIds.contains(rule.getId()))
                .toList();
            
            logger.info("同步统计: 添加 {} 条规则, 移除 {} 条规则, 检查更新 {} 条规则", 
                       rulesToAdd.size(), ruleIdsToRemove.size(), rulesToUpdate.size());
            
            boolean needRebuild = false;
            
            // 添加新规则
            for (DroolsRule rule : rulesToAdd) {
                try {
                    addRuleToFileSystem(rule);
                    needRebuild = true;
                    logger.debug("已添加规则到文件系统: {} (ID: {})", rule.getRuleName(), rule.getId());
                } catch (Exception e) {
                    logger.error("添加规则到文件系统失败: {} (ID: {})", rule.getRuleName(), rule.getId(), e);
                }
            }
            
            // 移除规则
            for (Long ruleId : ruleIdsToRemove) {
                try {
                    String rulePath = rulePathMap.get(ruleId);
                    if (rulePath != null) {
                        kieFileSystem.delete(rulePath);
                        rulePathMap.remove(ruleId);
                        needRebuild = true;
                        logger.debug("已从文件系统移除规则: ID {}", ruleId);
                    }
                } catch (Exception e) {
                    logger.error("从文件系统移除规则失败: ID {}", ruleId, e);
                }
            }
            
            // 更新规则（简化处理：重新添加所有需要更新的规则）
            for (DroolsRule rule : rulesToUpdate) {
                try {
                    String oldRulePath = rulePathMap.get(rule.getId());
                    if (oldRulePath != null) {
                        kieFileSystem.delete(oldRulePath);
                        addRuleToFileSystem(rule);
                        needRebuild = true;
                        logger.debug("已更新规则: {} (ID: {})", rule.getRuleName(), rule.getId());
                    }
                } catch (Exception e) {
                    logger.error("更新规则失败: {} (ID: {})", rule.getRuleName(), rule.getId(), e);
                }
            }
            
            // 如果有变化，重新构建容器
            if (needRebuild) {
                buildContainer();
                logger.info("容器同步完成，当前加载 {} 条规则", rulePathMap.size());
            } else {
                logger.info("容器与数据库已同步，无需更新");
            }
            
        } catch (Exception e) {
            logger.error("同步容器与数据库失败", e);
            throw new DroolsContainerException("Failed to sync container with database", e);
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * 容器错误恢复
     * 当容器出现错误时，尝试重新初始化
     */
    public void recoverContainer() {
        logger.warn("开始容器错误恢复...");
        
        try {
            writeLock.lock();
            
            // 清理当前状态
            if (kieContainer != null) {
                try {
                    kieContainer.dispose();
                } catch (Exception e) {
                    logger.warn("销毁旧容器时发生错误", e);
                }
            }
            
            kieContainer = null;
            kieFileSystem = null;
            rulePathMap.clear();
            
            // 重新初始化
            initializeContainer();
            
            logger.info("容器错误恢复完成");
            
        } catch (Exception e) {
            logger.error("容器错误恢复失败", e);
            throw new DroolsContainerException("Failed to recover container", e);
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * 获取容器健康状态
     * 
     * @return 容器健康状态信息
     */
    public ContainerHealthStatus getContainerHealthStatus() {
        readLock.lock();
        try {
            ContainerHealthStatus status = new ContainerHealthStatus();
            status.setInitialized(kieContainer != null);
            status.setLoadedRulesCount(rulePathMap.size());
            status.setLoadedRuleIds(getLoadedRuleIds());
            
            if (kieContainer != null) {
                try {
                    // 尝试创建一个KieSession来测试容器健康状态
                    KieSession testSession = kieContainer.newKieSession();
                    testSession.dispose();
                    status.setHealthy(true);
                    status.setStatusMessage("Container is healthy");
                } catch (Exception e) {
                    status.setHealthy(false);
                    status.setStatusMessage("Container error: " + e.getMessage());
                }
            } else {
                status.setHealthy(false);
                status.setStatusMessage("Container not initialized");
            }
            
            return status;
            
        } finally {
            readLock.unlock();
        }
    }

    /**
     * 服务销毁方法
     * 在Spring容器关闭前调用，清理资源
     */
    @PreDestroy
    public void destroy() {
        logger.info("开始销毁Drools容器服务...");
        
        try {
            writeLock.lock();
            
            if (kieContainer != null) {
                kieContainer.dispose();
                kieContainer = null;
            }
            
            kieFileSystem = null;
            rulePathMap.clear();
            
            logger.info("Drools容器服务销毁完成");
            
        } catch (Exception e) {
            logger.error("销毁Drools容器服务时发生错误", e);
        } finally {
            writeLock.unlock();
        }
    }
}