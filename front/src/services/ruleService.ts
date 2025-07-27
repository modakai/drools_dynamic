// API service for rule operations

import http from '@/utils/http'
import { API_ENDPOINTS, ERROR_MESSAGES } from '@/constants/api'
import { 
  isApiResponse, 
  isPageResponse, 
  isValidationResult,
  isValidId
} from '@/utils/typeGuards'
import {
  isValidRule,
  isValidUpdateRuleRequest,
  isValidTestRuleRequest,
  isValidPositiveInteger,
  isValidNonEmptyString
} from '@/utils/typeValidation'
import type { 
  DroolsRule, 
  UpdateRuleRequest, 
  TestRuleRequest,
  TestResult,
  ValidationResult,
  ApiResponse,
  PageResponse,
  RuleQueryParams,
  RuleStatistics
} from '@/types/rule'

/**
 * 规则API服务类
 * 提供所有规则相关的API操作，包括CRUD、测试和验证功能
 */
export class RuleService {
  private readonly baseUrl = API_ENDPOINTS.RULES

  /**
   * 获取所有规则
   * @returns Promise<DroolsRule[]> 规则列表
   */
  async getAllRules(): Promise<DroolsRule[]> {
    try {
      const { data }: ApiResponse<DroolsRule[]> = await http.get(this.baseUrl) 
      return data
    } catch (error) {
      console.error('Failed to fetch rules:', error)
      throw error
    }
  }

  /**
   * 分页获取规则
   * @param params 查询参数
   * @returns Promise<PageResponse<DroolsRule>> 分页规则数据
   */
  async getRulesPaginated(params: RuleQueryParams): Promise<PageResponse<DroolsRule>> {
    try {
      // 参数验证
      if (!isValidPositiveInteger(params.page) || !isValidPositiveInteger(params.size)) {
        throw new Error('Invalid pagination parameters')
      }

      const response: ApiResponse<ApiResponse<PageResponse<DroolsRule>>> = await http.get(this.baseUrl, {
        params: {
          page: params.page,
          size: params.size,
          sort: params.sort,
          order: params.order,
          ruleName: params.ruleName,
          enabled: params.enabled,
          createTimeStart: params.createTimeStart,
          createTimeEnd: params.createTimeEnd
        }
      })

      if (!isApiResponse<PageResponse<DroolsRule>>(response.data)) {
        throw new Error('Invalid API response format')
      }

      if (!response.data.success) {
        throw new Error(response.data.message || ERROR_MESSAGES.SERVER_ERROR)
      }

      const pageData = response.data.data
      if (!isPageResponse<DroolsRule>(pageData)) {
        throw new Error('Invalid page response format')
      }

      // 验证规则数据
      const validRules = pageData.content.filter(rule => isValidRule(rule))
      if (validRules.length !== pageData.content.length) {
        console.warn('Some rules have invalid format and were filtered out')
      }

      return {
        ...pageData,
        content: validRules
      }
    } catch (error) {
      console.error('Failed to fetch paginated rules:', error)
      throw error
    }
  }

  /**
   * 根据ID获取规则
   * @param id 规则ID
   * @returns Promise<DroolsRule> 规则对象
   */
  async getRuleById(id: number): Promise<DroolsRule> {
    try {
      // 参数验证
      if (!isValidId(id)) {
        throw new Error('Invalid rule ID')
      }

      const response: ApiResponse<DroolsRule> = await http.get(`${this.baseUrl}/${id}`)     

      if (!response.success) {
        throw new Error(response.message || ERROR_MESSAGES.SERVER_ERROR)
      }

      const rule = response.data
      if (!isValidRule(rule)) {
        throw new Error('Invalid rule data format')
      }

      return rule
    } catch (error) {
      console.error(`Failed to fetch rule with ID ${id}:`, error)
      throw error
    }
  }

  /**
   * 创建新规则
   * @param request 创建规则请求
   * @returns Promise<DroolsRule> 创建的规则对象
   */
  async createRule(request: DroolsRule): Promise<DroolsRule> {
    try {
      // 请求参数验证
      console.log('Create rule request:', request)
      console.log('Validation result:', {
        hasRequest: !!request,
        isObject: typeof request === 'object',
        hasRuleName: typeof request?.ruleName === 'string',
        ruleNameNotEmpty: request?.ruleName?.trim().length > 0,
        hasRuleContent: typeof request?.ruleContent === 'string',
        ruleContentNotEmpty: request?.ruleContent?.trim().length > 0,
        hasEnabled: typeof request?.enabled === 'boolean',
        hasDescription: request?.description === undefined || typeof request?.description === 'string'
      })
      
      // if (!isValidCreateRuleRequest(request)) {
      //   throw new Error('Invalid create rule request')
      // }

      const response: ApiResponse<ApiResponse<DroolsRule>> = await http.post(this.baseUrl, request)
      
      if (!isApiResponse<DroolsRule>(response.data)) {
        throw new Error('Invalid API response format')
      }

      if (!response.data.success) {
        throw new Error(response.data.message || ERROR_MESSAGES.SERVER_ERROR)
      }

      const rule = response.data.data
      if (!isValidRule(rule)) {
        throw new Error('Invalid rule data format in response')
      }

      return rule
    } catch (error) {
      console.error('Failed to create rule:', error)
      throw error
    }
  }

  /**
   * 更新规则
   * @param id 规则ID
   * @param request 更新规则请求
   * @returns Promise<DroolsRule> 更新后的规则对象
   */
  async updateRule(id: number, request: DroolsRule): Promise<DroolsRule> {
    try {
      const response: ApiResponse<DroolsRule> = await http.put(`${this.baseUrl}/${id}`, request)
      return response.data
    } catch (error) {
      console.error(`Failed to update rule with ID ${id}:`, error)
      throw error
    }
  }

  /**
   * 删除规则
   * @param id 规则ID
   * @returns Promise<void>
   */
  async deleteRule(id: number): Promise<void> {
    try {
      // 参数验证
      if (!isValidId(id)) {
        throw new Error('Invalid rule ID')
      }

      const response: ApiResponse<ApiResponse<void>> = await http.delete(`${this.baseUrl}/${id}`)
      
      if (!isApiResponse<void>(response.data)) {
        throw new Error('Invalid API response format')
      }

      if (!response.data.success) {
        throw new Error(response.data.message || ERROR_MESSAGES.SERVER_ERROR)
      }
    } catch (error) {
      console.error(`Failed to delete rule with ID ${id}:`, error)
      throw error
    }
  }

  /**
   * 批量删除规则
   * @param ids 规则ID数组
   * @returns Promise<void>
   */
  async deleteRules(ids: number[]): Promise<void> {
    try {
      // 参数验证
      if (!Array.isArray(ids) || ids.length === 0) {
        throw new Error('Invalid rule IDs array')
      }

      if (!ids.every(id => isValidId(id))) {
        throw new Error('Some rule IDs are invalid')
      }

      const response: ApiResponse<ApiResponse<void>> = await http.delete(this.baseUrl, {
        data: { ids }
      })
      
      if (!isApiResponse<void>(response.data)) {
        throw new Error('Invalid API response format')
      }

      if (!response.data.success) {
        throw new Error(response.data.message || ERROR_MESSAGES.SERVER_ERROR)
      }
    } catch (error) {
      console.error('Failed to delete rules:', error)
      throw error
    }
  }

  /**
   * 启用/禁用规则
   * @param id 规则ID
   * @param enabled 是否启用
   * @returns Promise<DroolsRule> 更新后的规则对象
   */
  async toggleRuleStatus(id: number, enabled: boolean): Promise<DroolsRule> {
    try {
      // 参数验证
      if (!isValidId(id)) {
        throw new Error('Invalid rule ID')
      }

      if (typeof enabled !== 'boolean') {
        throw new Error('Invalid enabled status')
      }

      const response: ApiResponse<ApiResponse<DroolsRule>> = await http.patch(`${this.baseUrl}/${id}/status`, {
        enabled
      })
      
      if (!isApiResponse<DroolsRule>(response.data)) {
        throw new Error('Invalid API response format')
      }

      if (!response.data.success) {
        throw new Error(response.data.message || ERROR_MESSAGES.SERVER_ERROR)
      }

      const rule = response.data.data
      if (!isValidRule(rule)) {
        throw new Error('Invalid rule data format in response')
      }

      return rule
    } catch (error) {
      console.error(`Failed to toggle rule status for ID ${id}:`, error)
      throw error
    }
  }

  /**
   * 验证规则语法
   * @param ruleContent 规则内容
   * @returns Promise<ValidationResult> 验证结果
   */
  async validateRule(ruleContent: string): Promise<ValidationResult> {
    try {
      // 参数验证
      if (!isValidNonEmptyString(ruleContent)) {
        throw new Error('Invalid rule content')
      }

      const response: ApiResponse<ValidationResult> = await http.post('/rules/test/validate', {ruleContent}, {
        headers: {
          'Content-Type': 'application/json',
        }
      })
    
      return response.data
    } catch (error) {
      console.error('Failed to validate rule:', error)
      throw error
    }
  }

  /**
   * 测试规则执行
   * @param request 测试请求
   * @returns Promise<TestResult> 测试结果
   */
  async testRule(request: TestRuleRequest): Promise<TestResult> {
    try {
     
      const response: ApiResponse<TestResult> = await http.post(API_ENDPOINTS.RULE_TEST, request)

      return response.data
    } catch (error) {
      console.error('Failed to test rule:', error)
      throw error
    }
  }

  /**
   * 获取规则统计信息
   * @returns Promise<RuleStatistics> 统计信息
   */
  async getRuleStatistics(): Promise<RuleStatistics> {
    try {
      const response: ApiResponse<ApiResponse<RuleStatistics>> = await http.get(`${this.baseUrl}/statistics`)
      
      if (!isApiResponse<RuleStatistics>(response.data)) {
        throw new Error('Invalid API response format')
      }

      if (!response.data.success) {
        throw new Error(response.data.message || ERROR_MESSAGES.SERVER_ERROR)
      }

      const statistics = response.data.data
      
      // 基本的统计信息验证
      if (!statistics || typeof statistics !== 'object') {
        throw new Error('Invalid statistics format')
      }

      return statistics as RuleStatistics
    } catch (error) {
      console.error('Failed to fetch rule statistics:', error)
      throw error
    }
  }

  /**
   * 导出规则
   * @param ids 规则ID数组，如果为空则导出所有规则
   * @returns Promise<Blob> 导出的文件数据
   */
  async exportRules(ids?: number[]): Promise<Blob> {
    try {
      // 参数验证
      if (ids && (!Array.isArray(ids) || !ids.every(id => isValidId(id)))) {
        throw new Error('Invalid rule IDs for export')
      }

      const response: ApiResponse<Blob> = await http.post(`${this.baseUrl}/export`, 
        { ids }, 
        { 
          responseType: 'blob',
          headers: {
            'Accept': 'application/octet-stream'
          }
        }
      )

      if (!(response.data instanceof Blob)) {
        throw new Error('Invalid export response format')
      }

      return response.data
    } catch (error) {
      console.error('Failed to export rules:', error)
      throw error
    }
  }

  /**
   * 导入规则
   * @param file 规则文件
   * @returns Promise<DroolsRule[]> 导入的规则列表
   */
  async importRules(file: File): Promise<DroolsRule[]> {
    try {
      // 参数验证
      if (!(file instanceof File)) {
        throw new Error('Invalid file for import')
      }

      const formData = new FormData()
      formData.append('file', file)

      const response: ApiResponse<ApiResponse<DroolsRule[]>> = await http.post(`${this.baseUrl}/import`, formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      })
      
      if (!isApiResponse<DroolsRule[]>(response.data)) {
        throw new Error('Invalid API response format')
      }

      if (!response.data.success) {
        throw new Error(response.data.message || ERROR_MESSAGES.SERVER_ERROR)
      }

      const rules = response.data.data
      if (!Array.isArray(rules)) {
        throw new Error('Invalid imported rules format')
      }

      // 验证导入的规则
      const validRules = rules.filter(rule => isValidRule(rule))
      if (validRules.length !== rules.length) {
        console.warn('Some imported rules have invalid format and were filtered out')
      }

      return validRules
    } catch (error) {
      console.error('Failed to import rules:', error)
      throw error
    }
  }
}

// 创建单例实例
export const ruleService = new RuleService()

// 默认导出
export default ruleService