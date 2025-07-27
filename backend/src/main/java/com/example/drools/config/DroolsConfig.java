package com.example.drools.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ComponentScan;

/**
 * Drools配置类
 * 配置Drools相关的组件和服务
 * 
 * @author System
 * @since 1.0.0
 */
@Configuration
@ComponentScan(basePackages = "com.example.drools.service")
public class DroolsConfig {
    
    // 这个配置类确保DroolsContainerService被正确扫描和初始化
    // 如果需要额外的Drools配置，可以在这里添加
}