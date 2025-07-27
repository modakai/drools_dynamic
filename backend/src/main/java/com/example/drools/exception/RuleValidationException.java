package com.example.drools.exception;

import java.util.List;

/**
 * 规则验证异常
 * 当规则语法验证失败时抛出此异常
 * 
 * @author System
 * @since 1.0.0
 */
public class RuleValidationException extends RuntimeException {

    private final List<ValidationError> errors;
    private final String ruleContent;
    private final Long ruleId;

    /**
     * 构造函数
     * 
     * @param message 异常消息
     */
    public RuleValidationException(String message) {
        super(message);
        this.errors = null;
        this.ruleContent = null;
        this.ruleId = null;
    }

    /**
     * 构造函数
     * 
     * @param message 异常消息
     * @param cause 原因异常
     */
    public RuleValidationException(String message, Throwable cause) {
        super(message, cause);
        this.errors = null;
        this.ruleContent = null;
        this.ruleId = null;
    }

    /**
     * 构造函数
     * 
     * @param message 异常消息
     * @param errors 验证错误列表
     */
    public RuleValidationException(String message, List<ValidationError> errors) {
        super(message);
        this.errors = errors;
        this.ruleContent = null;
        this.ruleId = null;
    }

    /**
     * 构造函数
     * 
     * @param message 异常消息
     * @param errors 验证错误列表
     * @param ruleContent 规则内容
     * @param ruleId 规则ID
     */
    public RuleValidationException(String message, List<ValidationError> errors, String ruleContent, Long ruleId) {
        super(message);
        this.errors = errors;
        this.ruleContent = ruleContent;
        this.ruleId = ruleId;
    }

    /**
     * 构造函数
     * 
     * @param message 异常消息
     * @param cause 原因异常
     * @param errors 验证错误列表
     * @param ruleContent 规则内容
     * @param ruleId 规则ID
     */
    public RuleValidationException(String message, Throwable cause, List<ValidationError> errors, String ruleContent, Long ruleId) {
        super(message, cause);
        this.errors = errors;
        this.ruleContent = ruleContent;
        this.ruleId = ruleId;
    }

    /**
     * 获取验证错误列表
     * 
     * @return 验证错误列表
     */
    public List<ValidationError> getErrors() {
        return errors;
    }

    /**
     * 获取规则内容
     * 
     * @return 规则内容
     */
    public String getRuleContent() {
        return ruleContent;
    }

    /**
     * 获取规则ID
     * 
     * @return 规则ID
     */
    public Long getRuleId() {
        return ruleId;
    }

    /**
     * 检查是否有验证错误
     * 
     * @return 是否有验证错误
     */
    public boolean hasErrors() {
        return errors != null && !errors.isEmpty();
    }

    /**
     * 获取错误数量
     * 
     * @return 错误数量
     */
    public int getErrorCount() {
        return errors != null ? errors.size() : 0;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("RuleValidationException{");
        sb.append("message='").append(getMessage()).append('\'');
        
        if (ruleId != null) {
            sb.append(", ruleId=").append(ruleId);
        }
        
        if (errors != null && !errors.isEmpty()) {
            sb.append(", errorCount=").append(errors.size());
        }
        
        sb.append('}');
        return sb.toString();
    }

    /**
     * 验证错误内部类
     */
    public static class ValidationError {
        private final String field;
        private final String message;
        private final String code;

        public ValidationError(String field, String message) {
            this.field = field;
            this.message = message;
            this.code = null;
        }

        public ValidationError(String field, String message, String code) {
            this.field = field;
            this.message = message;
            this.code = code;
        }

        public String getField() {
            return field;
        }

        public String getMessage() {
            return message;
        }

        public String getCode() {
            return code;
        }

        @Override
        public String toString() {
            return "ValidationError{" +
                    "field='" + field + '\'' +
                    ", message='" + message + '\'' +
                    ", code='" + code + '\'' +
                    '}';
        }
    }
}