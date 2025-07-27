package com.example.drools.dto;

import java.io.Serializable;

public class ValidateRuleRequest implements Serializable {

    private String ruleContent;

    public String getRuleContent() {
        return ruleContent;
    }

    public void setRuleContent(String ruleContent) {
        this.ruleContent = ruleContent;
    }

    @Override
    public String toString() {
        return "ValidateRuleRequest{" +
                "ruleContent='" + ruleContent + '\'' +
                '}';
    }
}
