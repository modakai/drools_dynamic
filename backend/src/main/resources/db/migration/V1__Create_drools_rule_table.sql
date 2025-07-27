-- Create drools_rule table for storing Drools rules
CREATE TABLE drools_rule (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    rule_name VARCHAR(255) NOT NULL UNIQUE COMMENT '规则名称',
    rule_content TEXT NOT NULL COMMENT '规则内容(DRL格式)',
    description TEXT COMMENT '规则描述',
    enabled BOOLEAN DEFAULT TRUE COMMENT '是否启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    version VARCHAR(50) DEFAULT '1.0' COMMENT '规则版本',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除标记(0-未删除,1-已删除)'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Drools规则表';

-- Create indexes for better query performance
CREATE INDEX idx_rule_name ON drools_rule(rule_name);
CREATE INDEX idx_enabled ON drools_rule(enabled);
CREATE INDEX idx_create_time ON drools_rule(create_time);
CREATE INDEX idx_deleted ON drools_rule(deleted);
