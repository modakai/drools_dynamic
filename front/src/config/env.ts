// 环境配置

export const ENV = {
  // API基础URL
  API_BASE_URL: import.meta.env.VITE_API_BASE_URL || '/api',
  
  // 应用配置
  APP_TITLE: 'Drools 动态规则管理系统',
  
  // 开发模式
  DEV: import.meta.env.DEV,
  
  // 生产模式
  PROD: import.meta.env.PROD,
  
  // 请求超时时间（毫秒）
  REQUEST_TIMEOUT: 10000,
  
  // 分页配置
  PAGE_SIZE: 10,
  PAGE_SIZE_OPTIONS: ['10', '20', '50', '100']
} as const