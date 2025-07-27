package com.example.drools.repository;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.drools.entity.DroolsRule;
import com.example.drools.mapper.DroolsRuleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Drools规则仓储类
 * 封装数据访问逻辑，提供业务层调用的接口
 * 
 * @author System
 * @since 1.0.0
 */
@Repository
public class DroolsRuleRepository {

    @Autowired
    private DroolsRuleMapper droolsRuleMapper;

    /**
     * 保存规则
     * 
     * @param rule 规则对象
     * @return 保存后的规则对象
     */
    public DroolsRule save(DroolsRule rule) {
        if (rule.getId() == null) {
            droolsRuleMapper.insert(rule);
        } else {
            droolsRuleMapper.updateById(rule);
        }
        return rule;
    }

    /**
     * 根据ID查找规则
     * 
     * @param id 规则ID
     * @return 规则对象，如果不存在则返回null
     */
    public DroolsRule findById(Long id) {
        return droolsRuleMapper.selectById(id);
    }

    /**
     * 根据规则名称查找规则
     * 
     * @param ruleName 规则名称
     * @return 规则对象，如果不存在则返回null
     */
    public DroolsRule findByRuleName(String ruleName) {
        return droolsRuleMapper.selectByRuleName(ruleName);
    }

    /**
     * 查找所有启用的规则
     * 
     * @return 启用的规则列表
     */
    public List<DroolsRule> findEnabledRules() {
        return droolsRuleMapper.selectEnabledRules();
    }

    /**
     * 查找所有禁用的规则
     * 
     * @return 禁用的规则列表
     */
    public List<DroolsRule> findDisabledRules() {
        return droolsRuleMapper.selectDisabledRules();
    }

    /**
     * 查找所有规则
     * 
     * @return 所有规则列表
     */
    public List<DroolsRule> findAll() {
        return droolsRuleMapper.selectList(null);
    }

    /**
     * 分页查询规则
     * 
     * @param pageNum 页码
     * @param pageSize 页面大小
     * @param enabled 是否启用（可选）
     * @param ruleName 规则名称关键字（可选）
     * @return 分页结果
     */
    public IPage<DroolsRule> findRulesPage(int pageNum, int pageSize, Boolean enabled, String ruleName) {
        Page<DroolsRule> page = new Page<>(pageNum, pageSize);
        return droolsRuleMapper.selectRulesPage(page, enabled, ruleName);
    }

    /**
     * 更新规则状态
     * 
     * @param id 规则ID
     * @param enabled 是否启用
     * @return 是否更新成功
     */
    public boolean updateRuleStatus(Long id, Boolean enabled) {
        return droolsRuleMapper.updateRuleStatus(id, enabled) > 0;
    }

    /**
     * 批量更新规则状态
     * 
     * @param ids 规则ID列表
     * @param enabled 是否启用
     * @return 更新的记录数
     */
    public int batchUpdateRuleStatus(List<Long> ids, Boolean enabled) {
        return droolsRuleMapper.batchUpdateRuleStatus(ids, enabled);
    }

    /**
     * 删除规则（逻辑删除）
     * 
     * @param id 规则ID
     * @return 是否删除成功
     */
    public boolean deleteById(Long id) {
        return droolsRuleMapper.deleteById(id) > 0;
    }

    /**
     * 批量删除规则（逻辑删除）
     * 
     * @param ids 规则ID列表
     * @return 删除的记录数
     */
    public int batchDeleteByIds(List<Long> ids) {
        return droolsRuleMapper.deleteBatchIds(ids);
    }

    /**
     * 检查规则名称是否存在
     * 
     * @param ruleName 规则名称
     * @param excludeId 排除的规则ID（用于更新时的唯一性检查）
     * @return 是否存在
     */
    public boolean isRuleNameExists(String ruleName, Long excludeId) {
        return droolsRuleMapper.checkRuleNameExists(ruleName, excludeId) > 0;
    }

    /**
     * 根据版本查询规则
     * 
     * @param version 版本号
     * @return 指定版本的规则列表
     */
    public List<DroolsRule> findByVersion(String version) {
        return droolsRuleMapper.selectByVersion(version);
    }

    /**
     * 搜索规则
     * 
     * @param keyword 关键字
     * @return 匹配的规则列表
     */
    public List<DroolsRule> searchRules(String keyword) {
        return droolsRuleMapper.searchRules(keyword);
    }

    /**
     * 获取规则总数
     * 
     * @return 规则总数
     */
    public long countRules() {
        return droolsRuleMapper.countRules();
    }

    /**
     * 获取启用规则总数
     * 
     * @return 启用规则总数
     */
    public long countEnabledRules() {
        return droolsRuleMapper.countEnabledRules();
    }

    /**
     * 获取最新创建的规则
     * 
     * @param limit 限制数量
     * @return 最新规则列表
     */
    public List<DroolsRule> findLatestRules(int limit) {
        return droolsRuleMapper.selectLatestRules(limit);
    }

    /**
     * 根据规则名称模糊查询
     * 
     * @param ruleName 规则名称关键字
     * @return 匹配的规则列表
     */
    public List<DroolsRule> findByRuleNameLike(String ruleName) {
        return droolsRuleMapper.selectByRuleNameLike(ruleName);
    }
}