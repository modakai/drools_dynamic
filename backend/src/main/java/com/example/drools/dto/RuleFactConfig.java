package com.example.drools.dto;

import java.util.Map;

// 规则表需要记录的额外字段
public class RuleFactConfig {
    private String factName;        // 事实名称（变量名，如$student）
    private String className;       // 全类名（如com.sakura.drools.domain.Student）
    private Map<String, Object> fieldValues; // 字段名和值的映射
    
    // constructors
    public RuleFactConfig() {}
    
    public RuleFactConfig(String factName, String className, Map<String, Object> fieldValues) {
        this.factName = factName;
        this.className = className;
        this.fieldValues = fieldValues;
    }
    
    // getters and setters
    public String getFactName() { return factName; }
    public void setFactName(String factName) { this.factName = factName; }
    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }
    public Map<String, Object> getFieldValues() { return fieldValues; }
    public void setFieldValues(Map<String, Object> fieldValues) { this.fieldValues = fieldValues; }
}