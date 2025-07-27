// 前端验证工具函数

/**
 * 验证规则名称
 * @param ruleName 规则名称
 * @returns 验证结果
 */
export const validateRuleName = (ruleName: string): { valid: boolean; message?: string } => {
  if (!ruleName || ruleName.trim().length === 0) {
    return { valid: false, message: '规则名称不能为空' }
  }
  
  if (ruleName.length > 255) {
    return { valid: false, message: '规则名称长度不能超过255个字符' }
  }
  
  // 检查特殊字符
  const invalidChars = /[<>:"/\\|?*]/
  if (invalidChars.test(ruleName)) {
    return { valid: false, message: '规则名称不能包含特殊字符 < > : " / \\ | ? *' }
  }
  
  return { valid: true }
}

/**
 * 验证规则内容
 * @param ruleContent 规则内容
 * @returns 验证结果
 */
export const validateRuleContent = (ruleContent: string): { valid: boolean; message?: string } => {
  if (!ruleContent || ruleContent.trim().length === 0) {
    return { valid: false, message: '规则内容不能为空' }
  }
  
  // 基本的Drools语法检查
  const hasRule = /rule\s+["'].*["']/i.test(ruleContent)
  const hasWhen = /when/i.test(ruleContent)
  const hasThen = /then/i.test(ruleContent)
  const hasEnd = /end/i.test(ruleContent)
  
  if (!hasRule) {
    return { valid: false, message: '规则内容必须包含rule声明' }
  }
  
  if (!hasWhen) {
    return { valid: false, message: '规则内容必须包含when条件' }
  }
  
  if (!hasThen) {
    return { valid: false, message: '规则内容必须包含then动作' }
  }
  
  if (!hasEnd) {
    return { valid: false, message: '规则内容必须以end结束' }
  }
  
  return { valid: true }
}

/**
 * 验证描述信息
 * @param description 描述信息
 * @returns 验证结果
 */
export const validateDescription = (description?: string): { valid: boolean; message?: string } => {
  if (description && description.length > 1000) {
    return { valid: false, message: '描述信息长度不能超过1000个字符' }
  }
  
  return { valid: true }
}

/**
 * 验证完整的规则对象
 * @param rule 规则对象
 * @returns 验证结果
 */
export const validateRule = (rule: {
  ruleName: string
  ruleContent: string
  description?: string
}): { valid: boolean; errors: string[] } => {
  const errors: string[] = []
  
  const nameValidation = validateRuleName(rule.ruleName)
  if (!nameValidation.valid && nameValidation.message) {
    errors.push(nameValidation.message)
  }
  
  const contentValidation = validateRuleContent(rule.ruleContent)
  if (!contentValidation.valid && contentValidation.message) {
    errors.push(contentValidation.message)
  }
  
  const descValidation = validateDescription(rule.description)
  if (!descValidation.valid && descValidation.message) {
    errors.push(descValidation.message)
  }
  
  return {
    valid: errors.length === 0,
    errors
  }
}