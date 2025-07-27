<template>
  <div class="rule-list-container">
    <!-- 搜索和操作栏 -->
    <div class="list-header">
      <div class="search-section">
        <a-form 
          layout="inline" 
          :model="searchForm"
          @finish="handleSearch"
        >
          <a-form-item label="规则名称">
            <a-input
              v-model:value="searchForm.ruleName"
              placeholder="请输入规则名称"
              allow-clear
              style="width: 200px"
            />
          </a-form-item>
          <a-form-item label="状态">
            <a-select
              v-model:value="searchForm.enabled"
              placeholder="请选择状态"
              allow-clear
              style="width: 120px"
            >
              <a-select-option :value="true">启用</a-select-option>
              <a-select-option :value="false">禁用</a-select-option>
            </a-select>
          </a-form-item>
          <a-form-item>
            <a-space>
              <a-button type="primary" html-type="submit" :loading="loading">
                <template #icon>
                  <SearchOutlined />
                </template>
                搜索
              </a-button>
              <a-button @click="handleReset">
                <template #icon>
                  <ReloadOutlined />
                </template>
                重置
              </a-button>
            </a-space>
          </a-form-item>
        </a-form>
      </div>
      
      <div class="action-section">
        <a-space>
          <a-button 
            :disabled="!hasSelected"
            @click="handleBatchDelete"
          >
            <template #icon>
              <DeleteOutlined />
            </template>
            批量删除
          </a-button>
          <a-button @click="handleRefresh" :loading="loading">
            <template #icon>
              <ReloadOutlined />
            </template>
            刷新
          </a-button>
        </a-space>
      </div>
    </div>

    <!-- 规则表格 -->
    <div class="table-section">
      <a-table
        :columns="columns"
        :data-source="rules"
        :loading="loading"
        :pagination="paginationConfig"
        :row-selection="rowSelection"
        :scroll="{ x: 1200 }"
        row-key="id"
        @change="handleTableChange"
      >
        <!-- 规则名称列 -->
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'ruleName'">
            <a-typography-text 
              :ellipsis="{ tooltip: record.ruleName }"
              strong
            >
              {{ record.ruleName }}
            </a-typography-text>
          </template>
          
          <!-- 规则内容列 -->
          <template v-else-if="column.key === 'ruleContent'">
            <a-typography-text 
              :ellipsis="{ tooltip: record.ruleContent }"
              code
              style="max-width: 300px"
            >
              {{ record.ruleContent }}
            </a-typography-text>
          </template>
          
          <!-- 描述列 -->
          <template v-else-if="column.key === 'description'">
            <a-typography-text 
              :ellipsis="{ tooltip: record.description }"
              type="secondary"
            >
              {{ record.description || '-' }}
            </a-typography-text>
          </template>
          
          <!-- 状态列 -->
          <template v-else-if="column.key === 'enabled'">
            <a-tag 
              :color="record.enabled ? 'success' : 'default'"
              @click="handleToggleStatus(record)"
              style="cursor: pointer"
            >
              <template #icon>
                <CheckCircleOutlined v-if="record.enabled" />
                <StopOutlined v-else />
              </template>
              {{ record.enabled ? '启用' : '禁用' }}
            </a-tag>
          </template>
          
          <!-- 版本列 -->
          <template v-else-if="column.key === 'version'">
            <a-tag color="blue">
              v{{ record.version || '1.0' }}
            </a-tag>
          </template>
          
          <!-- 创建时间列 -->
          <template v-else-if="column.key === 'createTime'">
            <a-typography-text type="secondary">
              {{ formatDate(record.createTime) }}
            </a-typography-text>
          </template>
          
          <!-- 操作列 -->
          <template v-else-if="column.key === 'actions'">
            <a-space>
              <a-tooltip title="编辑">
                <a-button 
                  type="text" 
                  size="small"
                  @click="handleEdit(record)"
                >
                  <template #icon>
                    <EditOutlined />
                  </template>
                </a-button>
              </a-tooltip>
              
              <a-tooltip title="测试">
                <a-button 
                  type="text" 
                  size="small"
                  @click="handleTest(record)"
                >
                  <template #icon>
                    <PlayCircleOutlined />
                  </template>
                </a-button>
              </a-tooltip>
              
              <a-tooltip title="复制">
                <a-button 
                  type="text" 
                  size="small"
                  @click="handleCopy(record)"
                >
                  <template #icon>
                    <CopyOutlined />
                  </template>
                </a-button>
              </a-tooltip>
              
              <a-popconfirm
                title="确定要删除这个规则吗？"
                ok-text="确定"
                cancel-text="取消"
                @confirm="handleDelete(record)"
              >
                <a-tooltip title="删除">
                  <a-button 
                    type="text" 
                    size="small"
                    danger
                  >
                    <template #icon>
                      <DeleteOutlined />
                    </template>
                  </a-button>
                </a-tooltip>
              </a-popconfirm>
            </a-space>
          </template>
        </template>
      </a-table>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, reactive, watch } from 'vue'
import { message } from 'ant-design-vue'
import {
  SearchOutlined,
  ReloadOutlined,
  DeleteOutlined,
  EditOutlined,
  PlayCircleOutlined,
  CopyOutlined,
  CheckCircleOutlined,
  StopOutlined
} from '@ant-design/icons-vue'
import type { TableColumnsType, TableProps } from 'ant-design-vue'
import type { RuleListProps, RuleListEmits } from '@/types/components'
import type { DroolsRule } from '@/types/rule'
import { formatDate } from '@/utils/date'
import type { Key } from 'ant-design-vue/es/vc-table/interface'

// 定义组件属性
const props = withDefaults(defineProps<RuleListProps>(), {
  rules: () => [],
  loading: false,
  selectedRowKeys: () => [],
  pagination: () => ({
    current: 1,
    pageSize: 10,
    total: 0,
    showSizeChanger: true,
    showQuickJumper: true
  })
})

// 定义组件事件
const emit = defineEmits<RuleListEmits>()

// 搜索表单
const searchForm = reactive({
  ruleName: '',
  enabled: undefined as boolean | undefined
})

// 选中的行
const selectedRowKeys = ref<Key[]>([])

// 计算属性
const hasSelected = computed(() => selectedRowKeys.value.length > 0)

const paginationConfig = computed(() => ({
  ...props.pagination,
  showTotal: (total: number, range: [number, number]) => 
    `第 ${range[0]}-${range[1]} 条，共 ${total} 条`,
  pageSizeOptions: ['10', '20', '50', '100']
}))

// 表格列配置
const columns: TableColumnsType = [
  {
    title: '规则名称',
    dataIndex: 'ruleName',
    key: 'ruleName',
    width: 200,
    fixed: 'left',
    sorter: true,
    ellipsis: true
  },
  {
    title: '规则内容',
    dataIndex: 'ruleContent',
    key: 'ruleContent',
    width: 300,
    ellipsis: true
  },
  {
    title: '描述',
    dataIndex: 'description',
    key: 'description',
    width: 200,
    ellipsis: true
  },
  {
    title: '状态',
    dataIndex: 'enabled',
    key: 'enabled',
    width: 100,
    align: 'center',
    filters: [
      { text: '启用', value: true },
      { text: '禁用', value: false }
    ]
  },
  {
    title: '版本',
    dataIndex: 'version',
    key: 'version',
    width: 100,
    align: 'center'
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
    key: 'createTime',
    width: 180,
    sorter: true
  },
  {
    title: '操作',
    key: 'actions',
    width: 200,
    fixed: 'right',
    align: 'center'
  }
]

// 行选择配置
const rowSelection: TableProps['rowSelection'] = {
  selectedRowKeys: selectedRowKeys,
  onChange: (keys: (string | number)[]) => {
    selectedRowKeys.value = keys
    emit('update:selectedRowKeys', keys)
  },
  onSelect: (record: DroolsRule, selected: boolean) => {
    console.log(`${record.ruleName} ${selected ? 'selected' : 'deselected'}`)
  },
  onSelectAll: (selected: boolean, selectedRows: DroolsRule[], changeRows: DroolsRule[]) => {
    console.log(`Select all ${selected}, selected rows: ${selectedRows.length}, change rows: ${changeRows.length}`)
  }
}

// 事件处理函数
const handleSearch = () => {
  const params = {
    ...searchForm,
    page: 1,
    size: props.pagination?.pageSize || 10
  }
  emit('pageChange', 1, props.pagination?.pageSize || 10)
  // 这里应该触发搜索事件，但由于类型定义中没有search事件，我们通过pageChange来处理
}

const handleReset = () => {
  searchForm.ruleName = ''
  searchForm.enabled = undefined
  handleSearch()
}

const handleRefresh = () => {
  emit('refresh')
}

const handleBatchDelete = () => {
  if (selectedRowKeys.value.length === 0) {
    message.warning('请选择要删除的规则')
    return
  }
  
  // 这里应该触发批量删除事件
  console.log('Batch delete:', selectedRowKeys.value)
}

const handleEdit = (record: DroolsRule) => {
  emit('edit', record)
  if (props.onEdit) {
    props.onEdit(record)
  }
}

const handleDelete = (record: DroolsRule) => {
  emit('delete', record)
  if (props.onDelete) {
    props.onDelete(record)
  }
}

const handleTest = (record: DroolsRule) => {
  emit('test', record)
  if (props.onTest) {
    props.onTest(record)
  }
}

const handleToggleStatus = (record: DroolsRule) => {
  emit('toggleStatus', record)
  if (props.onToggleStatus) {
    props.onToggleStatus(record)
  }
}

const handleCopy = async (record: DroolsRule) => {
  try {
    await navigator.clipboard.writeText(record.ruleContent)
    message.success('规则内容已复制到剪贴板')
  } catch (error) {
    console.error('复制失败:', error)
    message.error('复制失败')
  }
}

const handleTableChange: TableProps['onChange'] = (pagination) => {
  const { current = 1, pageSize = 10 } = pagination || {}
  emit('pageChange', current, pageSize)
}

// 监听属性变化
watch(() => props.selectedRowKeys, (newKeys) => {
  selectedRowKeys.value = newKeys || []
}, { immediate: true })
</script>

<style scoped>
.rule-list-container {
  background: #fff;
  border-radius: 8px;
  padding: 24px;
}

.list-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
  flex-wrap: wrap;
  gap: 16px;
}

.search-section {
  flex: 1;
  min-width: 0;
}

.action-section {
  flex-shrink: 0;
}

.table-section {
  margin-top: 16px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .list-header {
    flex-direction: column;
    align-items: stretch;
  }
  
  .search-section .ant-form {
    flex-direction: column;
  }
  
  .search-section .ant-form-item {
    margin-right: 0;
    margin-bottom: 16px;
  }
  
  .action-section {
    align-self: stretch;
  }
  
  .action-section .ant-space {
    width: 100%;
    justify-content: space-between;
  }
}

/* 表格样式优化 */
:deep(.ant-table-thead > tr > th) {
  background-color: #fafafa;
  font-weight: 600;
}

:deep(.ant-table-tbody > tr:hover > td) {
  background-color: #f5f5f5;
}

:deep(.ant-tag) {
  margin: 0;
}

/* 操作按钮样式 */
:deep(.ant-btn-text) {
  padding: 4px 8px;
  height: auto;
}

:deep(.ant-btn-text:hover) {
  background-color: rgba(0, 0, 0, 0.04);
}

:deep(.ant-btn-text.ant-btn-dangerous:hover) {
  background-color: rgba(255, 77, 79, 0.1);
}
</style>