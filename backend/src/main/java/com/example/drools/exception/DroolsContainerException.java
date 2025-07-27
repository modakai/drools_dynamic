package com.example.drools.exception;

/**
 * Drools容器相关异常
 * 当容器操作失败时抛出此异常
 * 
 * @author System
 * @since 1.0.0
 */
public class DroolsContainerException extends RuntimeException {

    private final String operation;
    private final Long ruleId;

    /**
     * 构造函数
     * 
     * @param message 异常消息
     */
    public DroolsContainerException(String message) {
        super(message);
        this.operation = null;
        this.ruleId = null;
    }

    /**
     * 构造函数
     * 
     * @param message 异常消息
     * @param cause 原因异常
     */
    public DroolsContainerException(String message, Throwable cause) {
        super(message, cause);
        this.operation = null;
        this.ruleId = null;
    }

    /**
     * 构造函数
     * 
     * @param message 异常消息
     * @param operation 操作类型
     * @param ruleId 规则ID
     */
    public DroolsContainerException(String message, String operation, Long ruleId) {
        super(message);
        this.operation = operation;
        this.ruleId = ruleId;
    }

    /**
     * 构造函数
     * 
     * @param message 异常消息
     * @param cause 原因异常
     * @param operation 操作类型
     * @param ruleId 规则ID
     */
    public DroolsContainerException(String message, Throwable cause, String operation, Long ruleId) {
        super(message, cause);
        this.operation = operation;
        this.ruleId = ruleId;
    }

    /**
     * 获取操作类型
     * 
     * @return 操作类型
     */
    public String getOperation() {
        return operation;
    }

    /**
     * 获取规则ID
     * 
     * @return 规则ID
     */
    public Long getRuleId() {
        return ruleId;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("DroolsContainerException{");
        sb.append("message='").append(getMessage()).append('\'');
        if (operation != null) {
            sb.append(", operation='").append(operation).append('\'');
        }
        if (ruleId != null) {
            sb.append(", ruleId=").append(ruleId);
        }
        sb.append('}');
        return sb.toString();
    }
}