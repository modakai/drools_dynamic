package com.example.drools.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.drools.entity.DroolsRule;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * Drools规则数据访问层接口
 * 继承MyBatis Plus的BaseMapper，提供基础CRUD操作
 * 
 * @author System
 * @since 1.0.0
 */
@Mapper
public interface DroolsRuleMapper extends BaseMapper<DroolsRule> {

    /**
     * 查询所有启用的规则
     * 
     * @return 启用的规则列表
     */
    @Select("SELECT * FROM drools_rule WHERE enabled = 1 AND deleted = 0 ORDER BY create_time DESC")
    List<DroolsRule> selectEnabledRules();

    /**
     * 查询所有禁用的规则
     * 
     * @return 禁用的规则列表
     */
    @Select("SELECT * FROM drools_rule WHERE enabled = 0 AND deleted = 0 ORDER BY create_time DESC")
    List<DroolsRule> selectDisabledRules();

    /**
     * 根据规则名称查询规则（精确匹配）
     * 
     * @param ruleName 规则名称
     * @return 规则对象，如果不存在则返回null
     */
    @Select("SELECT * FROM drools_rule WHERE rule_name = #{ruleName} AND deleted = 0")
    DroolsRule selectByRuleName(@Param("ruleName") String ruleName);

    /**
     * 根据规则名称模糊查询规则
     * 
     * @param ruleName 规则名称关键字
     * @return 匹配的规则列表
     */
    @Select("SELECT * FROM drools_rule WHERE rule_name LIKE CONCAT('%', #{ruleName}, '%') AND deleted = 0 ORDER BY create_time DESC")
    List<DroolsRule> selectByRuleNameLike(@Param("ruleName") String ruleName);

    /**
     * 更新规则状态（启用/禁用）
     * 
     * @param id 规则ID
     * @param enabled 是否启用
     * @return 影响的行数
     */
    @Update("UPDATE drools_rule SET enabled = #{enabled}, update_time = NOW() WHERE id = #{id} AND deleted = 0")
    int updateRuleStatus(@Param("id") Long id, @Param("enabled") Boolean enabled);

    /**
     * 批量更新规则状态
     * 
     * @param ids 规则ID列表
     * @param enabled 是否启用
     * @return 影响的行数
     */
    @Update("<script>" +
            "UPDATE drools_rule SET enabled = #{enabled}, update_time = NOW() " +
            "WHERE deleted = 0 AND id IN " +
            "<foreach collection='ids' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    int batchUpdateRuleStatus(@Param("ids") List<Long> ids, @Param("enabled") Boolean enabled);

    /**
     * 根据版本号查询规则
     * 
     * @param version 版本号
     * @return 指定版本的规则列表
     */
    @Select("SELECT * FROM drools_rule WHERE version = #{version} AND deleted = 0 ORDER BY create_time DESC")
    List<DroolsRule> selectByVersion(@Param("version") String version);

    /**
     * 查询规则总数（不包括已删除的）
     * 
     * @return 规则总数
     */
    @Select("SELECT COUNT(*) FROM drools_rule WHERE deleted = 0")
    long countRules();

    /**
     * 查询启用规则总数
     * 
     * @return 启用规则总数
     */
    @Select("SELECT COUNT(*) FROM drools_rule WHERE enabled = 1 AND deleted = 0")
    long countEnabledRules();

    /**
     * 分页查询规则
     * 
     * @param page 分页参数
     * @param enabled 是否启用（可选，null表示查询所有）
     * @param ruleName 规则名称关键字（可选）
     * @return 分页结果
     */
    @Select("<script>" +
            "SELECT * FROM drools_rule WHERE deleted = 0 " +
            "<if test='enabled != null'>" +
            "AND enabled = #{enabled} " +
            "</if>" +
            "<if test='ruleName != null and ruleName != \"\"'>" +
            "AND rule_name LIKE CONCAT('%', #{ruleName}, '%') " +
            "</if>" +
            "ORDER BY create_time DESC" +
            "</script>")
    IPage<DroolsRule> selectRulesPage(Page<DroolsRule> page, 
                                     @Param("enabled") Boolean enabled, 
                                     @Param("ruleName") String ruleName);

    /**
     * 检查规则名称是否存在（用于唯一性验证）
     * 
     * @param ruleName 规则名称
     * @param excludeId 排除的规则ID（用于更新时的唯一性检查）
     * @return 是否存在
     */
    @Select("<script>" +
            "SELECT COUNT(*) FROM drools_rule WHERE rule_name = #{ruleName} AND deleted = 0 " +
            "<if test='excludeId != null'>" +
            "AND id != #{excludeId} " +
            "</if>" +
            "</script>")
    int checkRuleNameExists(@Param("ruleName") String ruleName, @Param("excludeId") Long excludeId);

    /**
     * 获取最新创建的规则
     * 
     * @param limit 限制数量
     * @return 最新规则列表
     */
    @Select("SELECT * FROM drools_rule WHERE deleted = 0 ORDER BY create_time DESC LIMIT #{limit}")
    List<DroolsRule> selectLatestRules(@Param("limit") int limit);

    /**
     * 根据描述关键字搜索规则
     * 
     * @param keyword 关键字
     * @return 匹配的规则列表
     */
    @Select("SELECT * FROM drools_rule WHERE deleted = 0 AND " +
            "(rule_name LIKE CONCAT('%', #{keyword}, '%') OR " +
            "description LIKE CONCAT('%', #{keyword}, '%')) " +
            "ORDER BY create_time DESC")
    List<DroolsRule> searchRules(@Param("keyword") String keyword);
}