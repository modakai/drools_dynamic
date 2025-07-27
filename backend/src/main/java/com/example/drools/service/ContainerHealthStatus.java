package com.example.drools.service;

import java.util.List;

/**
 * 容器健康状态类
 * 封装Drools容器的健康状态信息
 * 
 * @author System
 * @since 1.0.0
 */
public class ContainerHealthStatus {
    
    private boolean initialized;
    private boolean healthy;
    private String statusMessage;
    private int loadedRulesCount;
    private List<Long> loadedRuleIds;

    /**
     * 默认构造函数
     */
    public ContainerHealthStatus() {
        this.initialized = false;
        this.healthy = false;
        this.statusMessage = "Not initialized";
        this.loadedRulesCount = 0;
    }

    /**
     * 构造函数
     * 
     * @param initialized 是否已初始化
     * @param healthy 是否健康
     * @param statusMessage 状态消息
     * @param loadedRulesCount 已加载规则数量
     */
    public ContainerHealthStatus(boolean initialized, boolean healthy, String statusMessage, int loadedRulesCount) {
        this.initialized = initialized;
        this.healthy = healthy;
        this.statusMessage = statusMessage;
        this.loadedRulesCount = loadedRulesCount;
    }

    /**
     * 检查容器是否已初始化
     * 
     * @return 是否已初始化
     */
    public boolean isInitialized() {
        return initialized;
    }

    /**
     * 设置初始化状态
     * 
     * @param initialized 是否已初始化
     */
    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    /**
     * 检查容器是否健康
     * 
     * @return 是否健康
     */
    public boolean isHealthy() {
        return healthy;
    }

    /**
     * 设置健康状态
     * 
     * @param healthy 是否健康
     */
    public void setHealthy(boolean healthy) {
        this.healthy = healthy;
    }

    /**
     * 获取状态消息
     * 
     * @return 状态消息
     */
    public String getStatusMessage() {
        return statusMessage;
    }

    /**
     * 设置状态消息
     * 
     * @param statusMessage 状态消息
     */
    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    /**
     * 获取已加载规则数量
     * 
     * @return 已加载规则数量
     */
    public int getLoadedRulesCount() {
        return loadedRulesCount;
    }

    /**
     * 设置已加载规则数量
     * 
     * @param loadedRulesCount 已加载规则数量
     */
    public void setLoadedRulesCount(int loadedRulesCount) {
        this.loadedRulesCount = loadedRulesCount;
    }

    /**
     * 获取已加载规则ID列表
     * 
     * @return 已加载规则ID列表
     */
    public List<Long> getLoadedRuleIds() {
        return loadedRuleIds;
    }

    /**
     * 设置已加载规则ID列表
     * 
     * @param loadedRuleIds 已加载规则ID列表
     */
    public void setLoadedRuleIds(List<Long> loadedRuleIds) {
        this.loadedRuleIds = loadedRuleIds;
    }

    /**
     * 检查容器是否完全正常（已初始化且健康）
     * 
     * @return 是否完全正常
     */
    public boolean isFullyOperational() {
        return initialized && healthy;
    }

    /**
     * 获取简要状态描述
     * 
     * @return 状态描述
     */
    public String getStatusSummary() {
        if (!initialized) {
            return "Not Initialized";
        } else if (!healthy) {
            return "Initialized but Unhealthy";
        } else {
            return "Healthy (" + loadedRulesCount + " rules loaded)";
        }
    }

    @Override
    public String toString() {
        return "ContainerHealthStatus{" +
                "initialized=" + initialized +
                ", healthy=" + healthy +
                ", statusMessage='" + statusMessage + '\'' +
                ", loadedRulesCount=" + loadedRulesCount +
                ", loadedRuleIds=" + loadedRuleIds +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ContainerHealthStatus that = (ContainerHealthStatus) o;

        if (initialized != that.initialized) return false;
        if (healthy != that.healthy) return false;
        if (loadedRulesCount != that.loadedRulesCount) return false;
        if (statusMessage != null ? !statusMessage.equals(that.statusMessage) : that.statusMessage != null)
            return false;
        return loadedRuleIds != null ? loadedRuleIds.equals(that.loadedRuleIds) : that.loadedRuleIds == null;
    }

    @Override
    public int hashCode() {
        int result = (initialized ? 1 : 0);
        result = 31 * result + (healthy ? 1 : 0);
        result = 31 * result + (statusMessage != null ? statusMessage.hashCode() : 0);
        result = 31 * result + loadedRulesCount;
        result = 31 * result + (loadedRuleIds != null ? loadedRuleIds.hashCode() : 0);
        return result;
    }
}