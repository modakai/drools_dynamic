package com.example.drools.exception;

import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * 业务逻辑异常
 * 用于处理业务逻辑相关的异常情况
 * 
 * @author System
 * @since 1.0.0
 */
public class BusinessException extends RuntimeException {

    private final String errorCode;
    private final HttpStatus httpStatus;
    private final Map<String, Object> context;

    /**
     * 构造函数
     * 
     * @param message 异常消息
     */
    public BusinessException(String message) {
        super(message);
        this.errorCode = "BUSINESS_ERROR";
        this.httpStatus = HttpStatus.BAD_REQUEST;
        this.context = new HashMap<>();
    }

    /**
     * 构造函数
     * 
     * @param message 异常消息
     * @param cause 原因异常
     */
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "BUSINESS_ERROR";
        this.httpStatus = HttpStatus.BAD_REQUEST;
        this.context = new HashMap<>();
    }

    /**
     * 构造函数
     * 
     * @param message 异常消息
     * @param errorCode 错误代码
     */
    public BusinessException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = HttpStatus.BAD_REQUEST;
        this.context = new HashMap<>();
    }

    /**
     * 构造函数
     * 
     * @param message 异常消息
     * @param errorCode 错误代码
     * @param httpStatus HTTP状态码
     */
    public BusinessException(String message, String errorCode, HttpStatus httpStatus) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
        this.context = new HashMap<>();
    }

    /**
     * 构造函数
     * 
     * @param message 异常消息
     * @param errorCode 错误代码
     * @param httpStatus HTTP状态码
     * @param context 上下文信息
     */
    public BusinessException(String message, String errorCode, HttpStatus httpStatus, Map<String, Object> context) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
        this.context = context != null ? new HashMap<>(context) : new HashMap<>();
    }

    /**
     * 构造函数
     * 
     * @param message 异常消息
     * @param cause 原因异常
     * @param errorCode 错误代码
     * @param httpStatus HTTP状态码
     * @param context 上下文信息
     */
    public BusinessException(String message, Throwable cause, String errorCode, HttpStatus httpStatus, Map<String, Object> context) {
        super(message, cause);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
        this.context = context != null ? new HashMap<>(context) : new HashMap<>();
    }

    /**
     * 获取错误代码
     * 
     * @return 错误代码
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * 获取HTTP状态码
     * 
     * @return HTTP状态码
     */
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    /**
     * 获取上下文信息
     * 
     * @return 上下文信息
     */
    public Map<String, Object> getContext() {
        return new HashMap<>(context);
    }

    /**
     * 添加上下文信息
     * 
     * @param key 键
     * @param value 值
     * @return 当前异常实例（支持链式调用）
     */
    public BusinessException addContext(String key, Object value) {
        this.context.put(key, value);
        return this;
    }

    /**
     * 添加多个上下文信息
     * 
     * @param contextMap 上下文映射
     * @return 当前异常实例（支持链式调用）
     */
    public BusinessException addContext(Map<String, Object> contextMap) {
        if (contextMap != null) {
            this.context.putAll(contextMap);
        }
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("BusinessException{");
        sb.append("message='").append(getMessage()).append('\'');
        sb.append(", errorCode='").append(errorCode).append('\'');
        sb.append(", httpStatus=").append(httpStatus);
        if (!context.isEmpty()) {
            sb.append(", context=").append(context);
        }
        sb.append('}');
        return sb.toString();
    }

    // 静态工厂方法，用于创建常见的业务异常

    /**
     * 创建规则状态异常
     * 
     * @param ruleId 规则ID
     * @param currentStatus 当前状态
     * @param expectedStatus 期望状态
     * @return 业务异常
     */
    public static BusinessException ruleStatusError(Long ruleId, String currentStatus, String expectedStatus) {
        return new BusinessException(
            String.format("规则状态错误，当前状态: %s，期望状态: %s", currentStatus, expectedStatus),
            "RULE_STATUS_ERROR",
            HttpStatus.CONFLICT
        ).addContext("ruleId", ruleId)
         .addContext("currentStatus", currentStatus)
         .addContext("expectedStatus", expectedStatus);
    }

    /**
     * 创建规则依赖异常
     * 
     * @param ruleId 规则ID
     * @param dependentRules 依赖的规则
     * @return 业务异常
     */
    public static BusinessException ruleDependencyError(Long ruleId, Object dependentRules) {
        return new BusinessException(
            "规则存在依赖关系，无法执行操作",
            "RULE_DEPENDENCY_ERROR",
            HttpStatus.CONFLICT
        ).addContext("ruleId", ruleId)
         .addContext("dependentRules", dependentRules);
    }

    /**
     * 创建规则执行异常
     * 
     * @param ruleId 规则ID
     * @param executionError 执行错误信息
     * @return 业务异常
     */
    public static BusinessException ruleExecutionError(Long ruleId, String executionError) {
        return new BusinessException(
            "规则执行失败: " + executionError,
            "RULE_EXECUTION_ERROR",
            HttpStatus.INTERNAL_SERVER_ERROR
        ).addContext("ruleId", ruleId)
         .addContext("executionError", executionError);
    }

    /**
     * 创建权限不足异常
     * 
     * @param operation 操作类型
     * @param resource 资源类型
     * @return 业务异常
     */
    public static BusinessException insufficientPermission(String operation, String resource) {
        return new BusinessException(
            String.format("权限不足，无法执行操作: %s %s", operation, resource),
            "INSUFFICIENT_PERMISSION",
            HttpStatus.FORBIDDEN
        ).addContext("operation", operation)
         .addContext("resource", resource);
    }

    /**
     * 创建配额超限异常
     * 
     * @param resourceType 资源类型
     * @param currentCount 当前数量
     * @param maxLimit 最大限制
     * @return 业务异常
     */
    public static BusinessException quotaExceeded(String resourceType, int currentCount, int maxLimit) {
        return new BusinessException(
            String.format("%s数量已达上限，当前: %d，最大: %d", resourceType, currentCount, maxLimit),
            "QUOTA_EXCEEDED",
            HttpStatus.CONFLICT
        ).addContext("resourceType", resourceType)
         .addContext("currentCount", currentCount)
         .addContext("maxLimit", maxLimit);
    }

    /**
     * 创建操作冲突异常
     * 
     * @param operation 操作类型
     * @param reason 冲突原因
     * @return 业务异常
     */
    public static BusinessException operationConflict(String operation, String reason) {
        return new BusinessException(
            String.format("操作冲突: %s，原因: %s", operation, reason),
            "OPERATION_CONFLICT",
            HttpStatus.CONFLICT
        ).addContext("operation", operation)
         .addContext("reason", reason);
    }
}