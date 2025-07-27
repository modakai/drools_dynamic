// TypeScript type definitions for components

/**
 * 代码编辑器组件属性类型
 */
export interface CodeEditorProps {
  modelValue?: string
  language?: string
  theme?: string
  height?: string | number
  width?: string | number
  readonly?: boolean
  showToolbar?: boolean
  placeholder?: string
  options?: Record<string, any>
  onSave?: (content: string) => void
}

/**
 * 代码编辑器组件事件类型
 */
export interface CodeEditorEmits {
  'update:modelValue': [value: string]
  'change': [value: string]
  'save': [content: string]
}

/**
 * 规则列表组件属性类型
 */
export interface RuleListProps {
  rules?: import('./rule').DroolsRule[]
  loading?: boolean
  selectedRowKeys?: (string | number)[]
  pagination?: {
    current: number
    pageSize: number
    total: number
    showSizeChanger?: boolean
    showQuickJumper?: boolean
  }
  searchKeyword?: string
  enabledFilter?: boolean | null
  onEdit?: (rule: import('./rule').DroolsRule) => void
  onDelete?: (rule: import('./rule').DroolsRule) => void
  onTest?: (rule: import('./rule').DroolsRule) => void
  onToggleStatus?: (rule: import('./rule').DroolsRule) => void
}

/**
 * 规则列表组件事件类型
 */
export interface RuleListEmits {
  'edit': [rule: import('./rule').DroolsRule]
  'delete': [rule: import('./rule').DroolsRule]
  'test': [rule: import('./rule').DroolsRule]
  'toggleStatus': [rule: import('./rule').DroolsRule]
  'refresh': []
  'pageChange': [page: number, pageSize: number]
  'update:selectedRowKeys': [keys: (string | number)[]]
}

/**
 * 规则编辑器组件属性类型
 */
export interface RuleEditorProps {
  ruleId?: number
  mode?: 'create' | 'edit' | 'view'
  initialData?: Partial<import('./rule').DroolsRule>
}

/**
 * 规则编辑器组件事件类型
 */
export interface RuleEditorEmits {
  'save': [rule: import('./rule').DroolsRule]
  'cancel': []
  'test': [rule: import('./rule').DroolsRule]
}

/**
 * 测试面板组件属性类型
 */
export interface TestPanelProps {
  ruleId?: number
  ruleContent?: string
  visible?: boolean
}

/**
 * 测试面板组件事件类型
 */
export interface TestPanelEmits {
  'close': []
  'test-complete': [result: import('./rule').TestResult]
}

/**
 * 表格列配置类型
 */
export interface TableColumn {
  key: string
  title: string
  dataIndex?: string
  width?: number | string
  align?: 'left' | 'center' | 'right'
  sorter?: boolean
  fixed?: 'left' | 'right'
  ellipsis?: boolean
  customRender?: (params: { text: any; record: any; index: number }) => any
}

/**
 * 表格操作按钮类型
 */
export interface TableAction {
  key: string
  label: string
  type?: 'primary' | 'default' | 'dashed' | 'link' | 'text'
  danger?: boolean
  disabled?: boolean | ((record: any) => boolean)
  onClick: (record: any) => void
}

/**
 * 搜索表单项类型
 */
export interface SearchFormItem {
  key: string
  label: string
  type: 'input' | 'select' | 'date' | 'dateRange'
  placeholder?: string
  options?: Array<{ label: string; value: any }>
  defaultValue?: any
}

/**
 * 分页配置类型
 */
export interface PaginationConfig {
  current: number
  pageSize: number
  total: number
  showSizeChanger?: boolean
  showQuickJumper?: boolean
  showTotal?: (total: number, range: [number, number]) => string
  onChange?: (page: number, pageSize: number) => void
}

/**
 * 模态框配置类型
 */
export interface ModalConfig {
  title: string
  width?: number | string
  centered?: boolean
  maskClosable?: boolean
  keyboard?: boolean
  destroyOnClose?: boolean
}

/**
 * 通知配置类型
 */
export interface NotificationConfig {
  type: 'success' | 'info' | 'warning' | 'error'
  title: string
  description?: string
  duration?: number
  placement?: 'topLeft' | 'topRight' | 'bottomLeft' | 'bottomRight'
}

/**
 * 面包屑项类型
 */
export interface BreadcrumbItem {
  title: string
  path?: string
  icon?: string
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
 * 工具栏按钮类型
 */
export interface ToolbarButton {
  key: string
  label: string
  icon?: string
  type?: 'primary' | 'default' | 'dashed' | 'link' | 'text'
  disabled?: boolean
  loading?: boolean
  onClick: () => void
}

/**
 * 状态标签类型
 */
export interface StatusTag {
  status: 'success' | 'processing' | 'error' | 'warning' | 'default'
  text: string
  color?: string
}

/**
 * 文件上传配置类型
 */
export interface UploadConfig {
  accept?: string
  multiple?: boolean
  maxSize?: number
  maxCount?: number
  beforeUpload?: (file: File) => boolean | Promise<boolean>
  onChange?: (fileList: File[]) => void
}

/**
 * 规则表单数据类型
 */
export interface RuleFormData {
  id?: number
  ruleName: string
  ruleContent: string
  description?: string
  enabled: boolean
  version?: string
}

/**
 * 规则表单事件类型
 */
export interface RuleFormEmits {
  'save': [rule: import('./rule').DroolsRule]
  'cancel': []
  'validate': [valid: boolean, errors?: string[]]
}