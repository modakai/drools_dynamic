package com.example.drools.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 更新规则请求DTO
 * 
 * @author System
 * @since 1.0.0
 */
public class UpdateRuleRequest {

    /**
     * 规则名称
     */
    @NotBlank(message = "规则名称不能为空")
    @Size(max = 255, message = "规则名称长度不能超过255个字符")
    private String ruleName;

    /**
     * 规则内容
     */
    @NotBlank(message = "规则内容不能为空")
    private String ruleContent;

    /**
     * 规则描述
     */
    @Size(max = 1000, message = "规则描述长度不能超过1000个字符")
    private String description;

    /**
     * 是否启用
     */
    @NotNull(message = "启用状态不能为空")
    private Boolean enabled;

    /**
     * 规则版本（用于乐观锁）
     */
    @NotBlank(message = "版本号不能为空")
    @Size(max = 50, message = "版本号长度不能超过50个字符")
    private String version;

    // 默认构造函数
    public UpdateRuleRequest() {
    }

    // 带参构造函数
    public UpdateRuleRequest(String ruleName, String ruleContent, String description, Boolean enabled, String version) {
        this.ruleName = ruleName;
        this.ruleContent = ruleContent;
        this.description = description;
        this.enabled = enabled;
        this.version = version;
    }

    // Getter and Setter methods
    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getRuleContent() {
        return ruleContent;
    }

    public void setRuleContent(String ruleContent) {
        this.ruleContent = ruleContent;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "UpdateRuleRequest{" +
                "ruleName='" + ruleName + '\'' +
                ", ruleContent='" + ruleContent + '\'' +
                ", description='" + description + '\'' +
                ", enabled=" + enabled +
                ", version='" + version + '\'' +
                '}';
    }
}