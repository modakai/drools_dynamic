-- Create rule_test_log table (optional)
CREATE TABLE rule_test_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    rule_id BIGINT COMMENT '规则ID',
    test_data JSON COMMENT '测试数据',
    test_result JSON COMMENT '测试结果',
    execution_time BIGINT COMMENT '执行时间(毫秒)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除标记',
    
    INDEX idx_rule_id (rule_id),
    INDEX idx_create_time (create_time),
    INDEX idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='规则测试日志表';