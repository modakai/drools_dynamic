package com.example.drools.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class ReflectionUtil {
    
    private static final Logger logger = LoggerFactory.getLogger(ReflectionUtil.class);
    
    /**
     * 根据类名和字段值创建对象实例
     */
    public static Object createInstance(String className, Map<String, Object> fieldValues) {
        try {
            // 加载类
            Class<?> clazz = Class.forName(className);
            
            // 创建实例
            Object instance = clazz.getDeclaredConstructor().newInstance();
            
            // 设置字段值
            setFieldValues(instance, clazz, fieldValues);
            
            return instance;
            
        } catch (Exception e) {
            logger.error("创建实例失败: className={}, fieldValues={}", className, fieldValues, e);
            throw new RuntimeException("创建实例失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 设置对象字段值
     */
    private static void setFieldValues(Object instance, Class<?> clazz, Map<String, Object> fieldValues) {
        for (Map.Entry<String, Object> entry : fieldValues.entrySet()) {
            String fieldName = entry.getKey();
            Object fieldValue = entry.getValue();
            
            try {
                // 查找字段（包括父类字段）
                Field field = findField(clazz, fieldName);
                if (field != null) {
                    field.setAccessible(true);
                    
                    // 类型转换
                    Object convertedValue = convertValue(fieldValue, field.getType());
                    field.set(instance, convertedValue);
                } else {
                    logger.warn("字段 {} 在类 {} 中未找到", fieldName, clazz.getName());
                }
                
            } catch (Exception e) {
                logger.error("设置字段 {} 值失败: {}", fieldName, fieldValue, e);
                throw new RuntimeException("设置字段值失败: " + fieldName, e);
            }
        }
    }
    
    /**
     * 查找字段（包括父类）
     */
    private static Field findField(Class<?> clazz, String fieldName) {
        while (clazz != null) {
            try {
                return clazz.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            }
        }
        return null;
    }
    
    /**
     * 类型转换
     */
    private static Object convertValue(Object value, Class<?> targetType) {
        if (value == null || targetType.isInstance(value)) {
            return value;
        }
        
        String stringValue = value.toString();
        
        // 基本类型转换
        if (targetType == String.class) {
            return stringValue;
        } else if (targetType == int.class || targetType == Integer.class) {
            return Integer.parseInt(stringValue);
        } else if (targetType == long.class || targetType == Long.class) {
            return Long.parseLong(stringValue);
        } else if (targetType == double.class || targetType == Double.class) {
            return Double.parseDouble(stringValue);
        } else if (targetType == float.class || targetType == Float.class) {
            return Float.parseFloat(stringValue);
        } else if (targetType == boolean.class || targetType == Boolean.class) {
            return Boolean.parseBoolean(stringValue);
        } else if (targetType == Date.class) {
            // 简单处理日期转换
            try {
                return new SimpleDateFormat("yyyy-MM-dd").parse(stringValue);
            } catch (ParseException e) {
                throw new RuntimeException("日期格式错误: " + stringValue);
            }
        }
        
        return value;
    }
}