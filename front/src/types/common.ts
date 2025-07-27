// 通用类型定义

/**
 * 基础实体类型
 */
export interface BaseEntity {
  id?: number
  createTime?: string
  updateTime?: string
}

/**
 * 操作状态枚举
 */
export enum OperationStatus {
  IDLE = 'idle',
  LOADING = 'loading',
  SUCCESS = 'success',
  ERROR = 'error'
}

/**
 * 排序方向枚举
 */
export enum SortOrder {
  ASC = 'asc',
  DESC = 'desc'
}

/**
 * 表格列配置类型
 */
export interface TableColumn {
  key: string
  title: string
  dataIndex: string
  width?: number
  align?: 'left' | 'center' | 'right'
  sortable?: boolean
  filterable?: boolean
  fixed?: 'left' | 'right'
  ellipsis?: boolean
}

/**
 * 表单字段类型
 */
export interface FormField {
  name: string
  label: string
  type: 'input' | 'textarea' | 'select' | 'switch' | 'date' | 'number'
  required?: boolean
  placeholder?: string
  rules?: FormRule[]
  options?: SelectOption[]
}

/**
 * 表单验证规则类型
 */
export interface FormRule {
  required?: boolean
  message?: string
  min?: number
  max?: number
  pattern?: RegExp
  validator?: (rule: any, value: any) => Promise<void>
}

/**
 * 选择器选项类型
 */
export interface SelectOption {
  label: string
  value: string | number
  disabled?: boolean
}

/**
 * 菜单项类型
 */
export interface MenuItem {
  key: string
  label: string
  icon?: string
  path?: string
  children?: MenuItem[]
  disabled?: boolean
}

/**
 * 面包屑项类型
 */
export interface BreadcrumbItem {
  title: string
  path?: string
}

/**
 * 通知消息类型
 */
export interface NotificationMessage {
  type: 'success' | 'info' | 'warning' | 'error'
  title: string
  message?: string
  duration?: number
}

/**
 * 模态框配置类型
 */
export interface ModalConfig {
  title: string
  content?: string
  width?: number
  centered?: boolean
  maskClosable?: boolean
  keyboard?: boolean
  okText?: string
  cancelText?: string
  onOk?: () => void | Promise<void>
  onCancel?: () => void
}