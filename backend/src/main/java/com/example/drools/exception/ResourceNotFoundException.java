package com.example.drools.exception;

/**
 * 资源不存在异常
 * 当请求的资源（如规则、用户等）不存在时抛出此异常
 * 
 * @author System
 * @since 1.0.0
 */
public class ResourceNotFoundException extends RuntimeException {

    private final String resourceType;
    private final Object resourceId;

    /**
     * 构造函数
     * 
     * @param message 异常消息
     */
    public ResourceNotFoundException(String message) {
        super(message);
        this.resourceType = null;
        this.resourceId = null;
    }

    /**
     * 构造函数
     * 
     * @param message 异常消息
     * @param cause 原因异常
     */
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
        this.resourceType = null;
        this.resourceId = null;
    }

    /**
     * 构造函数
     * 
     * @param resourceType 资源类型
     * @param resourceId 资源ID
     */
    public ResourceNotFoundException(String resourceType, Object resourceId) {
        super(String.format("%s不存在: %s", resourceType, resourceId));
        this.resourceType = resourceType;
        this.resourceId = resourceId;
    }

    /**
     * 构造函数
     * 
     * @param message 异常消息
     * @param resourceType 资源类型
     * @param resourceId 资源ID
     */
    public ResourceNotFoundException(String message, String resourceType, Object resourceId) {
        super(message);
        this.resourceType = resourceType;
        this.resourceId = resourceId;
    }

    /**
     * 构造函数
     * 
     * @param message 异常消息
     * @param cause 原因异常
     * @param resourceType 资源类型
     * @param resourceId 资源ID
     */
    public ResourceNotFoundException(String message, Throwable cause, String resourceType, Object resourceId) {
        super(message, cause);
        this.resourceType = resourceType;
        this.resourceId = resourceId;
    }

    /**
     * 获取资源类型
     * 
     * @return 资源类型
     */
    public String getResourceType() {
        return resourceType;
    }

    /**
     * 获取资源ID
     * 
     * @return 资源ID
     */
    public Object getResourceId() {
        return resourceId;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ResourceNotFoundException{");
        sb.append("message='").append(getMessage()).append('\'');
        if (resourceType != null) {
            sb.append(", resourceType='").append(resourceType).append('\'');
        }
        if (resourceId != null) {
            sb.append(", resourceId=").append(resourceId);
        }
        sb.append('}');
        return sb.toString();
    }

    // 静态工厂方法，用于创建常见的资源不存在异常

    /**
     * 创建规则不存在异常
     * 
     * @param ruleId 规则ID
     * @return 资源不存在异常
     */
    public static ResourceNotFoundException ruleNotFound(Long ruleId) {
        return new ResourceNotFoundException("规则", ruleId);
    }

    /**
     * 创建规则不存在异常（按名称）
     * 
     * @param ruleName 规则名称
     * @return 资源不存在异常
     */
    public static ResourceNotFoundException ruleNotFoundByName(String ruleName) {
        return new ResourceNotFoundException("规则", ruleName);
    }

    /**
     * 创建用户不存在异常
     * 
     * @param userId 用户ID
     * @return 资源不存在异常
     */
    public static ResourceNotFoundException userNotFound(Long userId) {
        return new ResourceNotFoundException("用户", userId);
    }

    /**
     * 创建配置不存在异常
     * 
     * @param configKey 配置键
     * @return 资源不存在异常
     */
    public static ResourceNotFoundException configNotFound(String configKey) {
        return new ResourceNotFoundException("配置", configKey);
    }
}