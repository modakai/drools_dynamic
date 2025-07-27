package com.example.drools.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 规则测试结果DTO
 * 
 * @author System
 * @since 1.0.0
 */
public class TestResult {

    /**
     * 测试是否成功
     */
    private Boolean success;

    /**
     * 执行时间（毫秒）
     */
    private Long executionTime;

    /**
     * 触发的规则数量
     */
    private Integer firedRulesCount;

    /**
     * 触发的规则详情
     */
    private List<FiredRuleInfo> firedRules;

    /**
     * 测试后的数据状态
     */
    private Map<String, Object> resultData;

    /**
     * 错误信息（如果测试失败）
     */
    private String errorMessage;

    /**
     * 错误堆栈跟踪（如果测试失败）
     */
    private String stackTrace;

    /**
     * 测试时间
     */
    private LocalDateTime testTime;

    /**
     * 详细执行信息
     */
    private String executionDetails;

    // 默认构造函数
    public TestResult() {
        this.testTime = LocalDateTime.now();
    }

    // 成功结果构造函数
    public TestResult(Long executionTime, Integer firedRulesCount, List<FiredRuleInfo> firedRules, Map<String, Object> resultData) {
        this();
        this.success = true;
        this.executionTime = executionTime;
        this.firedRulesCount = firedRulesCount;
        this.firedRules = firedRules;
        this.resultData = resultData;
    }

    // 失败结果构造函数
    public TestResult(String errorMessage, String stackTrace) {
        this();
        this.success = false;
        this.errorMessage = errorMessage;
        this.stackTrace = stackTrace;
        this.firedRulesCount = 0;
    }

    // Getter and Setter methods
    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Long getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(Long executionTime) {
        this.executionTime = executionTime;
    }

    public Integer getFiredRulesCount() {
        return firedRulesCount;
    }

    public void setFiredRulesCount(Integer firedRulesCount) {
        this.firedRulesCount = firedRulesCount;
    }

    public List<FiredRuleInfo> getFiredRules() {
        return firedRules;
    }

    public void setFiredRules(List<FiredRuleInfo> firedRules) {
        this.firedRules = firedRules;
    }

    public Map<String, Object> getResultData() {
        return resultData;
    }

    public void setResultData(Map<String, Object> resultData) {
        this.resultData = resultData;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }

    public LocalDateTime getTestTime() {
        return testTime;
    }

    public void setTestTime(LocalDateTime testTime) {
        this.testTime = testTime;
    }

    public String getExecutionDetails() {
        return executionDetails;
    }

    public void setExecutionDetails(String executionDetails) {
        this.executionDetails = executionDetails;
    }

    @Override
    public String toString() {
        return "TestResult{" +
                "success=" + success +
                ", executionTime=" + executionTime +
                ", firedRulesCount=" + firedRulesCount +
                ", firedRules=" + firedRules +
                ", resultData=" + resultData +
                ", errorMessage='" + errorMessage + '\'' +
                ", stackTrace='" + stackTrace + '\'' +
                ", testTime=" + testTime +
                ", executionDetails='" + executionDetails + '\'' +
                '}';
    }

    /**
     * 触发规则信息内部类
     */
    public static class FiredRuleInfo {
        private String ruleName;
        private String rulePackage;
        private Long ruleId;
        private String description;

        public FiredRuleInfo() {
        }

        public FiredRuleInfo(String ruleName, String rulePackage) {
            this.ruleName = ruleName;
            this.rulePackage = rulePackage;
        }

        public FiredRuleInfo(String ruleName, String rulePackage, Long ruleId, String description) {
            this.ruleName = ruleName;
            this.rulePackage = rulePackage;
            this.ruleId = ruleId;
            this.description = description;
        }

        // Getter and Setter methods
        public String getRuleName() {
            return ruleName;
        }

        public void setRuleName(String ruleName) {
            this.ruleName = ruleName;
        }

        public String getRulePackage() {
            return rulePackage;
        }

        public void setRulePackage(String rulePackage) {
            this.rulePackage = rulePackage;
        }

        public Long getRuleId() {
            return ruleId;
        }

        public void setRuleId(Long ruleId) {
            this.ruleId = ruleId;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        @Override
        public String toString() {
            return "FiredRuleInfo{" +
                    "ruleName='" + ruleName + '\'' +
                    ", rulePackage='" + rulePackage + '\'' +
                    ", ruleId=" + ruleId +
                    ", description='" + description + '\'' +
                    '}';
        }
    }
}