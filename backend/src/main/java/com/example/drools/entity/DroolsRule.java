package com.example.drools.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

/**
 * Drools规则实体类
 * 对应数据库表 drools_rule
 * 
 * @author System
 * @since 1.0.0
 */
@TableName("drools_rule")
public class DroolsRule {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 规则名称，必须唯一
     */
    @TableField("rule_name")
    private String ruleName;

    /**
     * 规则内容，DRL格式
     */
    @TableField("rule_content")
    private String ruleContent;

    /**
     * 规则描述
     */
    @TableField("description")
    private String description;

    /**
     * 是否启用，默认为true
     */
    @TableField("enabled")
    private Boolean enabled;

    /**
     * 创建时间，自动填充
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 更新时间，自动填充
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    /**
     * 规则版本
     */
    @TableField("version")
    private String version;

    /**
     * 逻辑删除标记
     */
    @TableLogic
    @TableField("deleted")
    private Integer deleted;

    // 默认构造函数
    public DroolsRule() {
        this.enabled = true;
        this.version = "1.0";
        this.deleted = 0;
    }

    // 带参构造函数
    public DroolsRule(String ruleName, String ruleContent, String description) {
        this();
        this.ruleName = ruleName;
        this.ruleContent = ruleContent;
        this.description = description;
    }

    // Getter and Setter methods
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }

    @Override
    public String toString() {
        return "DroolsRule{" +
                "id=" + id +
                ", ruleName='" + ruleName + '\'' +
                ", ruleContent='" + ruleContent + '\'' +
                ", description='" + description + '\'' +
                ", enabled=" + enabled +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", version='" + version + '\'' +
                ", deleted=" + deleted +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DroolsRule that = (DroolsRule) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        return ruleName != null ? ruleName.equals(that.ruleName) : that.ruleName == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (ruleName != null ? ruleName.hashCode() : 0);
        return result;
    }
}