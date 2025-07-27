package com.example.drools.exception;

import com.example.drools.dto.ApiResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 * 统一处理应用程序中的各种异常，并返回标准化的API响应格式
 * 
 * @author System
 * @since 1.0.0
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理规则验证异常
     * 
     * @param e 规则验证异常
     * @return 统一响应格式
     */
    @ExceptionHandler(RuleValidationException.class)
    public ResponseEntity<ApiResponse<Map<String, Object>>> handleRuleValidationException(RuleValidationException e) {
        logger.warn("规则验证失败: {}", e.getMessage());
        
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("ruleId", e.getRuleId());
        errorDetails.put("ruleContent", e.getRuleContent());
        errorDetails.put("errorCount", e.getErrorCount());
        
        if (e.hasErrors()) {
            errorDetails.put("validationErrors", e.getErrors());
        }
        
        ApiResponse<Map<String, Object>> response = new ApiResponse<>(
            422, 
            "规则语法验证失败: " + e.getMessage(), 
            errorDetails, 
            false
        );
        
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(response);
    }

    /**
     * 处理Drools容器异常
     * 
     * @param e Drools容器异常
     * @return 统一响应格式
     */
    @ExceptionHandler(DroolsContainerException.class)
    public ResponseEntity<ApiResponse<Map<String, Object>>> handleDroolsContainerException(DroolsContainerException e) {
        logger.error("Drools容器操作失败: {}", e.getMessage(), e);
        
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("operation", e.getOperation());
        errorDetails.put("ruleId", e.getRuleId());
        
        ApiResponse<Map<String, Object>> response = new ApiResponse<>(
            500, 
            "规则容器操作失败: " + e.getMessage(), 
            errorDetails, 
            false
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    /**
     * 处理数据完整性违反异常（如唯一约束冲突）
     * 
     * @param e 数据完整性违反异常
     * @return 统一响应格式
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        logger.warn("数据完整性约束违反: {}", e.getMessage());
        
        String message = "数据操作失败";
        if (e.getMessage() != null) {
            if (e.getMessage().contains("Duplicate entry")) {
                message = "数据已存在，请检查唯一性约束";
            } else if (e.getMessage().contains("foreign key constraint")) {
                message = "外键约束违反，请检查关联数据";
            } else if (e.getMessage().contains("not null")) {
                message = "必填字段不能为空";
            }
        }
        
        ApiResponse<Void> response = new ApiResponse<>(409, message, null, false);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    /**
     * 处理参数验证异常（@Valid注解触发）
     * 
     * @param e 方法参数验证异常
     * @return 统一响应格式
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        logger.warn("请求参数验证失败: {}", e.getMessage());
        
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        ApiResponse<Map<String, String>> response = new ApiResponse<>(
            400, 
            "请求参数验证失败", 
            errors, 
            false
        );
        
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * 处理绑定异常
     * 
     * @param e 绑定异常
     * @return 统一响应格式
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleBindException(BindException e) {
        logger.warn("数据绑定失败: {}", e.getMessage());
        
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        ApiResponse<Map<String, String>> response = new ApiResponse<>(
            400, 
            "数据绑定失败", 
            errors, 
            false
        );
        
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * 处理约束违反异常
     * 
     * @param e 约束违反异常
     * @return 统一响应格式
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleConstraintViolationException(ConstraintViolationException e) {
        logger.warn("约束验证失败: {}", e.getMessage());
        
        Map<String, String> errors = new HashMap<>();
        for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
            String fieldName = violation.getPropertyPath().toString();
            String errorMessage = violation.getMessage();
            errors.put(fieldName, errorMessage);
        }
        
        ApiResponse<Map<String, String>> response = new ApiResponse<>(
            400, 
            "约束验证失败", 
            errors, 
            false
        );
        
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * 处理非法参数异常
     * 
     * @param e 非法参数异常
     * @return 统一响应格式
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(IllegalArgumentException e) {
        logger.warn("非法参数: {}", e.getMessage());
        
        ApiResponse<Void> response = new ApiResponse<>(400, e.getMessage(), null, false);
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * 处理缺少请求参数异常
     * 
     * @param e 缺少请求参数异常
     * @return 统一响应格式
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        logger.warn("缺少请求参数: {}", e.getMessage());
        
        String message = String.format("缺少必需的请求参数: %s", e.getParameterName());
        ApiResponse<Void> response = new ApiResponse<>(400, message, null, false);
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * 处理方法参数类型不匹配异常
     * 
     * @param e 方法参数类型不匹配异常
     * @return 统一响应格式
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        logger.warn("参数类型不匹配: {}", e.getMessage());
        
        String message = String.format("参数 '%s' 的值 '%s' 类型不正确，期望类型: %s", 
            e.getName(), e.getValue(), e.getRequiredType().getSimpleName());
        ApiResponse<Void> response = new ApiResponse<>(400, message, null, false);
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * 处理HTTP消息不可读异常（如JSON格式错误）
     * 
     * @param e HTTP消息不可读异常
     * @return 统一响应格式
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        logger.warn("HTTP消息不可读: {}", e.getMessage());
        
        String message = "请求体格式错误，请检查JSON格式";
        if (e.getMessage() != null && e.getMessage().contains("JSON parse error")) {
            message = "JSON格式错误，请检查请求体格式";
        }
        
        ApiResponse<Void> response = new ApiResponse<>(400, message, null, false);
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * 处理不支持的HTTP方法异常
     * 
     * @param e 不支持的HTTP方法异常
     * @return 统一响应格式
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        logger.warn("不支持的HTTP方法: {}", e.getMessage());
        
        String message = String.format("不支持的HTTP方法: %s，支持的方法: %s", 
            e.getMethod(), String.join(", ", e.getSupportedMethods()));
        ApiResponse<Void> response = new ApiResponse<>(405, message, null, false);
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(response);
    }

    /**
     * 处理不支持的媒体类型异常
     * 
     * @param e 不支持的媒体类型异常
     * @return 统一响应格式
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException e) {
        logger.warn("不支持的媒体类型: {}", e.getMessage());
        
        String message = String.format("不支持的媒体类型: %s，支持的类型: %s", 
            e.getContentType(), e.getSupportedMediaTypes().stream()
                .map(Object::toString)
                .collect(Collectors.joining(", ")));
        ApiResponse<Void> response = new ApiResponse<>(415, message, null, false);
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(response);
    }

    /**
     * 处理404异常（找不到处理器）
     * 
     * @param e 找不到处理器异常
     * @return 统一响应格式
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNoHandlerFoundException(NoHandlerFoundException e) {
        logger.warn("找不到请求处理器: {}", e.getMessage());
        
        String message = String.format("请求的资源不存在: %s %s", e.getHttpMethod(), e.getRequestURL());
        ApiResponse<Void> response = new ApiResponse<>(404, message, null, false);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    /**
     * 处理资源不存在异常
     * 
     * @param e 资源不存在异常
     * @return 统一响应格式
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Map<String, Object>>> handleResourceNotFoundException(ResourceNotFoundException e) {
        logger.warn("资源不存在: {}", e.getMessage());
        
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("resourceType", e.getResourceType());
        errorDetails.put("resourceId", e.getResourceId());
        
        ApiResponse<Map<String, Object>> response = new ApiResponse<>(
            404, 
            e.getMessage(), 
            errorDetails, 
            false
        );
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    /**
     * 处理业务逻辑异常
     * 
     * @param e 业务逻辑异常
     * @return 统一响应格式
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Map<String, Object>>> handleBusinessException(BusinessException e) {
        logger.warn("业务逻辑异常: {}", e.getMessage());
        
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("errorCode", e.getErrorCode());
        errorDetails.put("context", e.getContext());
        
        ApiResponse<Map<String, Object>> response = new ApiResponse<>(
            e.getHttpStatus().value(), 
            e.getMessage(), 
            errorDetails, 
            false
        );
        
        return ResponseEntity.status(e.getHttpStatus()).body(response);
    }

    /**
     * 处理所有其他未捕获的异常
     * 
     * @param e 通用异常
     * @return 统一响应格式
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception e) {
        logger.error("未处理的异常: {}", e.getMessage(), e);
        
        // 在生产环境中，不应该暴露详细的异常信息
        String message = "服务器内部错误，请稍后重试";
        
        // 在开发环境中可以显示更多信息
        if (logger.isDebugEnabled()) {
            message = "服务器内部错误: " + e.getMessage();
        }
        
        ApiResponse<Void> response = new ApiResponse<>(500, message, null, false);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}