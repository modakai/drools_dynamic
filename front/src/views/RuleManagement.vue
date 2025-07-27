<template>
  <div class="rule-management">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">规则管理</h1>
        <p class="page-description">管理和维护业务规则，支持创建、编辑、删除和测试规则</p>
      </div>
      <div class="header-actions">
        <a-button 
          type="primary" 
          size="large"
          @click="handleCreateRule"
          :loading="loading"
        >
          <template #icon>
            <PlusOutlined />
          </template>
          创建规则
        </a-button>
      </div>
    </div>

    <!-- 搜索和筛选区域 -->
    <div class="search-section">
      <a-row :gutter="16">
        <a-col :span="8">
          <a-input-search
            v-model:value="searchKeyword"
            placeholder="搜索规则名称或描述"
            allow-clear
            @search="handleSearch"
            @change="handleSearchChange"
          />
        </a-col>
        <a-col :span="4">
          <a-select
            v-model:value="statusFilter"
            placeholder="状态筛选"
            allow-clear
            style="width: 100%"
            @change="handleStatusFilter"
          >
            <a-select-option value="enabled">启用</a-select-option>
            <a-select-option value="disabled">禁用</a-select-option>
          </a-select>
        </a-col>
        <a-col :span="4">
          <a-button @click="handleRefresh" :loading="loading">
            <template #icon>
              <ReloadOutlined />
            </template>
            刷新
          </a-button>
        </a-col>
      </a-row>
    </div>

    <!-- 规则列表 -->
    <div class="list-section">
      <a-spin :spinning="loading" tip="加载中...">
        <RuleList
          :rules="filteredRules"
          :loading="loading"
          :pagination="{
            current: 1,
            pageSize: 10,
            total: filteredRules.length,
            showSizeChanger: true,
            showQuickJumper: true
          }"
          @edit="handleEditRule"
          @delete="handleDeleteRule"
          @toggleStatus="handleToggleStatus"
          @test="handleTestRule"
          @refresh="handleRefresh"
        />
      </a-spin>
    </div>

  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { PlusOutlined, ReloadOutlined } from '@ant-design/icons-vue'
import RuleList from '@/components/RuleList.vue'
import { useLoading } from '@/composables/useLoading'
import { useConfirm } from '@/composables/useConfirm'
import { ruleService } from '@/services'
import type { DroolsRule } from '@/types/rule'

const router = useRouter()
const { loading, withLoading } = useLoading()
const { confirmDelete } = useConfirm()

// 响应式数据
const rules = ref<DroolsRule[]>([])
const searchKeyword = ref('')
const statusFilter = ref<string | undefined>(undefined)
const error = ref<string | null>(null)

// 计算属性 - 过滤后的规则列表
const filteredRules = computed(() => {
  let filtered = rules.value

  // 关键词搜索
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    filtered = filtered.filter(rule => 
      rule.ruleName.toLowerCase().includes(keyword) ||
      (rule.description && rule.description.toLowerCase().includes(keyword))
    )
  }

  // 状态筛选
  if (statusFilter.value) {
    const enabled = statusFilter.value === 'enabled'
    filtered = filtered.filter(rule => rule.enabled === enabled)
  }

  return filtered
})

// 页面方法
const loadRules = async () => {
  try {
    const data = await withLoading(() => ruleService.getAllRules())
    rules.value = data
    clearError()
  } catch (err) {
    console.error('加载规则列表失败:', err)
    error.value = '加载规则列表失败，请稍后重试'
    message.error('加载规则列表失败')
  }
}

const handleCreateRule = () => {
  router.push('/rules/create')
}

const handleEditRule = (rule: DroolsRule) => {
  router.push(`/rules/${rule.id}/edit`)
}

const handleDeleteRule = async (rule: DroolsRule) => {
  confirmDelete({
    title: '确认删除规则',
    content: `确定要删除规则 "${rule.ruleName}" 吗？删除后无法恢复。`,
    onOk: async () => {
      try {
        await withLoading(() => ruleService.deleteRule(rule.id!))
        message.success('规则删除成功')
        await loadRules() // 重新加载列表
      } catch (err) {
        console.error('删除规则失败:', err)
        message.error('删除规则失败')
      }
    }
  })
}

const handleToggleStatus = async (rule: DroolsRule) => {
  try {
    const updatedRule = {
      ...rule,
      enabled: !rule.enabled
    }
    
    await withLoading(() => ruleService.updateRule(rule.id!, updatedRule))
    
    const action = updatedRule.enabled ? '启用' : '禁用'
    message.success(`规则${action}成功`)
    
    await loadRules() // 重新加载列表
  } catch (err) {
    console.error('更新规则状态失败:', err)
    message.error('更新规则状态失败')
  }
}

const handleTestRule = (rule: DroolsRule) => {
  router.push({
    path: '/rules/test',
    query: { ruleId: rule.id?.toString() }
  })
}

const handleSearch = () => {
  // 搜索逻辑已通过计算属性实现
}

const handleSearchChange = () => {
  // 实时搜索，无需额外处理
}

const handleStatusFilter = () => {
  // 筛选逻辑已通过计算属性实现
}

const handleRefresh = async () => {
  await loadRules()
  message.success('刷新成功')
}

const clearError = () => {
  error.value = null
}

// 监听搜索关键词变化，实现实时搜索
watch(searchKeyword, () => {
  // 计算属性会自动更新
}, { immediate: false })

// 页面挂载时加载数据
onMounted(() => {
  loadRules()
})
</script>

<style scoped>
.rule-management {
  padding: 0;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.header-content {
  flex: 1;
}

.page-title {
  margin: 0 0 8px 0;
  font-size: 24px;
  font-weight: 600;
  color: #262626;
}

.page-description {
  margin: 0;
  color: #8c8c8c;
  font-size: 14px;
}

.header-actions {
  flex-shrink: 0;
}

.search-section {
  margin-bottom: 24px;
  padding: 16px;
  background: #fafafa;
  border-radius: 6px;
}

.list-section {
  margin-bottom: 24px;
}

.empty-state {
  padding: 48px 0;
  text-align: center;
}

.error-alert {
  margin-top: 16px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    align-items: stretch;
  }
  
  .header-actions {
    margin-top: 16px;
  }
  
  .search-section .ant-row .ant-col {
    margin-bottom: 8px;
  }
}
</style>