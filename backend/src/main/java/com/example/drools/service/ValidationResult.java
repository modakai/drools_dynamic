package com.example.drools.service;

/**
 * 规则验证结果类
 * 封装规则语法验证的结果信息
 * 
 * @author System
 * @since 1.0.0
 */
public class ValidationResult {
    
    private final boolean valid;
    private final String errorMessage;
    private final String warningMessage;

    /**
     * 构造函数 - 仅包含验证结果和错误信息
     * 
     * @param valid 是否验证通过
     * @param errorMessage 错误信息（如果验证失败）
     */
    public ValidationResult(boolean valid, String errorMessage) {
        this.valid = valid;
        this.errorMessage = errorMessage;
        this.warningMessage = null;
    }

    /**
     * 构造函数 - 包含验证结果、错误信息和警告信息
     * 
     * @param valid 是否验证通过
     * @param errorMessage 错误信息（如果验证失败）
     * @param warningMessage 警告信息（如果有警告）
     */
    public ValidationResult(boolean valid, String errorMessage, String warningMessage) {
        this.valid = valid;
        this.errorMessage = errorMessage;
        this.warningMessage = warningMessage;
    }

    /**
     * 检查验证是否通过
     * 
     * @return 是否验证通过
     */
    public boolean isValid() {
        return valid;
    }

    /**
     * 获取错误信息
     * 
     * @return 错误信息，如果验证通过则返回null
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * 获取警告信息
     * 
     * @return 警告信息，如果没有警告则返回null
     */
    public String getWarningMessage() {
        return warningMessage;
    }

    /**
     * 检查是否有警告
     * 
     * @return 是否有警告
     */
    public boolean hasWarning() {
        return warningMessage != null && !warningMessage.trim().isEmpty();
    }

    /**
     * 检查是否有错误
     * 
     * @return 是否有错误
     */
    public boolean hasError() {
        return !valid && errorMessage != null && !errorMessage.trim().isEmpty();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ValidationResult{");
        sb.append("valid=").append(valid);
        
        if (errorMessage != null) {
            sb.append(", errorMessage='").append(errorMessage).append('\'');
        }
        
        if (warningMessage != null) {
            sb.append(", warningMessage='").append(warningMessage).append('\'');
        }
        
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ValidationResult that = (ValidationResult) o;

        if (valid != that.valid) return false;
        if (errorMessage != null ? !errorMessage.equals(that.errorMessage) : that.errorMessage != null)
            return false;
        return warningMessage != null ? warningMessage.equals(that.warningMessage) : that.warningMessage == null;
    }

    @Override
    public int hashCode() {
        int result = (valid ? 1 : 0);
        result = 31 * result + (errorMessage != null ? errorMessage.hashCode() : 0);
        result = 31 * result + (warningMessage != null ? warningMessage.hashCode() : 0);
        return result;
    }
}