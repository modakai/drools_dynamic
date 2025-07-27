package com.example.drools.dto;

import java.util.List;

/**
 * 规则测试请求DTO
 * 
 * @author System
 * @since 1.0.0
 */
public class TestRuleRequest {

    /**
     * 配置
     */
    private List<RuleFactConfig> ruleConfigs;

    /**
     * 要测试的规则ID列表（可选，如果提供则测试指定规则）
     */
    private List<Long> ruleIds;

    /**
     * 是否返回详细的执行信息
     */
    private Boolean verbose;

    /**
     * 最大执行时间（毫秒）
     */
    private Long maxExecutionTime;

    // 默认构造函数
    public TestRuleRequest() {
        this.verbose = false;
        this.maxExecutionTime = 30000L; // 默认30秒超时
    }

    public List<Long> getRuleIds() {
        return ruleIds;
    }

    public void setRuleIds(List<Long> ruleIds) {
        this.ruleIds = ruleIds;
    }

    public Boolean getVerbose() {
        return verbose;
    }

    public void setVerbose(Boolean verbose) {
        this.verbose = verbose;
    }

    public Long getMaxExecutionTime() {
        return maxExecutionTime;
    }

    public void setMaxExecutionTime(Long maxExecutionTime) {
        this.maxExecutionTime = maxExecutionTime;
    }

    public List<RuleFactConfig> getRuleConfigs() {
        return ruleConfigs;
    }

    public void setRuleConfigs(List<RuleFactConfig> ruleConfigs) {
        this.ruleConfigs = ruleConfigs;
    }

    @Override
    public String toString() {
        return "TestRuleRequest{" +
                ", ruleIds=" + ruleIds +
                ", verbose=" + verbose +
                ", maxExecutionTime=" + maxExecutionTime +
                '}';
    }
}