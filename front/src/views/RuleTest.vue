<template>
  <div class="rule-test">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">规则测试</h1>
        <p class="page-description">测试规则执行效果，验证业务逻辑是否符合预期</p>
      </div>
      <div class="header-actions">
        <a-space>
          <a-button @click="handleClearAll" :disabled="loading">
            <template #icon>
              <ClearOutlined />
            </template>
            清空
          </a-button>
          <a-button type="primary" @click="handleRunTest" :loading="loading" :disabled="!canRunTest">
            <template #icon>
              <PlayCircleOutlined />
            </template>
            执行测试
          </a-button>
        </a-space>
      </div>
    </div>

    <!-- 主要内容区域 -->
    <div class="test-content">
      <a-row :gutter="24">
        <!-- 左侧：测试配置 -->
        <a-col :span="12">
          <div class="test-config-section">
            <h3 class="section-title">测试配置</h3>

            <!-- 规则选择 -->
            <div class="config-item">
              <label class="config-label">选择规则：</label>
              <a-select v-model:value="selectedRuleIds" mode="multiple" placeholder="选择要测试的规则" style="width: 100%"
                :loading="loadingRules" :options="ruleOptions" @change="handleRuleSelectionChange" />
              <div class="config-help">
                必须选择要测试的规则
              </div>
            </div>

            <!-- 测试数据输入 -->
            <div class="config-item">
              <label class="config-label">测试数据：</label>
              <div class="fact-config-section">
                <div class="fact-config-help">
                  <a-alert message="配置规则测试所需的 Fact 对象" description="需要指定完整的类名和字段值" type="info" show-icon
                    style="margin-bottom: 16px" />
                </div>

                <div class="fact-configs">
                  <div v-for="(config, index) in ruleConfigs" :key="index" class="fact-config-item">
                    <div class="fact-config-header">
                      <h4>Fact 对象 {{ index + 1 }}</h4>
                      <a-button type="text" danger size="small" @click="removeFactConfig(index)"
                        v-if="ruleConfigs.length > 1">
                        <template #icon>
                          <DeleteOutlined />
                        </template>
                        删除
                      </a-button>
                    </div>

                    <a-form layout="vertical">
                      <a-form-item label="类全路径名称" required>
                        <a-input v-model:value="config.className" placeholder="如: com.example.drools.entity.Order" />
                      </a-form-item>

                      <a-form-item label="字段值">
                        <div class="field-values">
                          <div v-for="(field, fieldIndex) in config.fields" :key="fieldIndex" class="field-value-row">
                            <a-input v-model:value="field.key" placeholder="字段名" style="width: 40%" />
                            <a-input v-model:value="field.value" placeholder="字段值"
                              style="width: 40%; margin-left: 8px" />
                            <a-button type="text" danger size="small" @click="removeFieldValue(index, fieldIndex)"
                              style="margin-left: 8px" v-if="config.fields.length > 1">
                              <template #icon>
                                <DeleteOutlined />
                              </template>
                            </a-button>
                          </div>
                          <a-button type="dashed" size="small" @click="addFieldValue(index)"
                            style="width: 100%; margin-top: 8px">
                            <template #icon>
                              <PlusOutlined />
                            </template>
                            添加字段
                          </a-button>
                        </div>
                      </a-form-item>
                    </a-form>
                  </div>

                  <a-button type="dashed" @click="addFactConfig" style="width: 100%; margin-top: 16px">
                    <template #icon>
                      <PlusOutlined />
                    </template>
                    添加 Fact 对象
                  </a-button>
                </div>
              </div>
            </div>

            <!-- 测试选项 -->
            <div class="config-item">
              <label class="config-label">测试选项：</label>
              <a-space direction="vertical" style="width: 100%">
                <a-checkbox v-model:checked="testOptions.showExecutionDetails">
                  显示执行详情
                </a-checkbox>

                <a-checkbox v-model:checked="testOptions.validateInput">
                  验证输入数据
                </a-checkbox>
              </a-space>
            </div>
          </div>
        </a-col>

        <!-- 右侧：测试结果 -->
        <a-col :span="12">
          <div class="test-result-section">
            <div class="section-header">
              <h3 class="section-title">测试结果</h3>
              <div class="result-actions" v-if="testResult">
                <a-tooltip title="导出结果">
                  <a-button size="small" @click="handleExportResult">
                    <template #icon>
                      <ExportOutlined />
                    </template>
                  </a-button>
                </a-tooltip>
                <a-tooltip title="清空结果">
                  <a-button size="small" @click="handleClearResult">
                    <template #icon>
                      <ClearOutlined />
                    </template>
                  </a-button>
                </a-tooltip>
              </div>
            </div>

            <!-- 测试结果显示 -->
            <div class="result-content">
              <div v-if="!testResult && !loading" class="empty-result">
                <a-empty :image="Empty.PRESENTED_IMAGE_SIMPLE" description="暂无测试结果">
                  <p class="empty-tip">配置测试参数后点击"执行测试"查看结果</p>
                </a-empty>
              </div>

              <div v-if="loading" class="loading-result">
                <a-spin size="large" tip="正在执行测试...">
                  <div class="loading-placeholder"></div>
                </a-spin>
              </div>

              <div v-if="testResult" class="result-display">
                <!-- 执行摘要 -->
                <div class="result-summary">
                  <a-statistic-group>
                    <a-statistic title="执行时间" :value="testResult.executionTime" suffix="ms" />
                    <a-statistic title="触发规则" :value="testResult.firedRulesCount || 0" suffix="个" />
                    <a-statistic title="执行状态" :value="testResult.success ? '成功' : '失败'"
                      :value-style="{ color: testResult.success ? '#3f8600' : '#cf1322' }" />
                  </a-statistic-group>
                </div>

                <!-- 触发的规则 -->
                <div class="fired-rules" v-if="testResult.firedRules?.length">
                  <h4>触发的规则：</h4>
                  <a-list :data-source="testResult.firedRules" size="small" bordered>
                    <template #renderItem="{ item }">
                      <a-list-item>
                        <a-list-item-meta>
                          <template #title>
                            <a-tag color="blue">{{ item.ruleName }}</a-tag>
                          </template>
                          <template #description>
                            {{ item.description || '无描述' }}
                          </template>
                        </a-list-item-meta>
                        <template #actions>
                          <a @click="viewRuleDetails(item)">查看详情</a>
                        </template>
                      </a-list-item>
                    </template>
                  </a-list>
                </div>

                <!-- 执行结果数据 -->
                <!-- <div class="result-data">
                  <h4>执行结果：</h4>
                  <CodeEditor
                    v-model:modelValue="JSON.stringify(testResult.resultData)"
                    language="json"
                    :height="200"
                    :readonly="true"
                  />
                </div> -->

                <!-- 执行详情 -->
                <!-- <div class="execution-details" v-if="testOptions.showExecutionDetails && testResult.executionDetails">
                  <h4>执行详情：</h4>
                  <a-collapse>
                    <a-collapse-panel key="logs" header="执行日志">
                      <pre class="execution-logs">{{ testResult.executionDetails.logs }}</pre>
                    </a-collapse-panel>
                    <a-collapse-panel key="facts" header="事实对象">
                      <CodeEditor
                        v-model:modelValue="JSON.stringify(testResult.executionDetails.facts, null, 2)"
                        language="json"
                        :height="150"
                        :readonly="true"
                      />
                    </a-collapse-panel>
                  </a-collapse>
                </div> -->

                <!-- 错误信息 -->
                <div class="error-info" v-if="!testResult.success && testResult.errorMessage">
                  <a-alert type="error" :message="testResult.errorMessage" :description="testResult.errorDetails"
                    show-icon />
                </div>
              </div>
            </div>
          </div>
        </a-col>
      </a-row>
    </div>



    <!-- 错误提示 -->
    <a-alert v-if="error" :message="error" type="error" show-icon closable @close="clearError" class="error-alert" />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { message, Empty } from 'ant-design-vue'
import {
  PlayCircleOutlined,
  ClearOutlined,
  ExportOutlined,
  DeleteOutlined,
  PlusOutlined
} from '@ant-design/icons-vue'

import { useLoading } from '@/composables/useLoading'
import { useConfirm } from '@/composables/useConfirm'
import { ruleService } from '@/services'

import type {
  DroolsRule,
  TestRuleRequest,
  TestResult,
  RuleFactConfig
} from '@/types/rule'

const route = useRoute()
const { loading, withLoading } = useLoading()
const { confirm } = useConfirm()

// 页面状态
const rules = ref<DroolsRule[]>([])
const loadingRules = ref(false)
const selectedRuleIds = ref<number[]>([])
const testOptions = ref({
  showExecutionDetails: true,
  validateInput: true
})

// RuleFactConfig 配置
const ruleConfigs = ref([
  {
    className: '',
    fields: [{ key: '', value: '' }]
  }
])

// 测试结果
const testResult = ref<TestResult | null>(null)
const error = ref<string | null>(null)





// 计算属性
const ruleOptions = computed(() =>
  rules.value.map(rule => ({
    label: rule.ruleName,
    value: rule.id!,
    disabled: !rule.enabled
  }))
)

const canRunTest = computed(() => {
  // 必须选择规则
  if (selectedRuleIds.value.length === 0) {
    return false
  }

  // 检查是否有有效的 Fact 配置
  const hasTestData = ruleConfigs.value.some(config =>
    config.className.trim() &&
    config.fields.some(field => field.key.trim() && field.value.trim())
  )

  return hasTestData && !loading.value
})



// 页面方法
const loadRules = async () => {
  try {
    loadingRules.value = true
    const data = await ruleService.getAllRules()
    rules.value = data

    // 如果URL中有ruleId参数，自动选中该规则
    const ruleId = route.query.ruleId
    if (ruleId) {
      const id = Number(ruleId)
      if (rules.value.some(rule => rule.id === id)) {
        selectedRuleIds.value = [id]
      }
    }
  } catch (err) {
    console.error('加载规则列表失败:', err)
    message.error('加载规则列表失败')
  } finally {
    loadingRules.value = false
  }
}

const handleRunTest = async () => {
  try {
    // 验证必须选择规则
    if (selectedRuleIds.value.length === 0) {
      message.error('请选择要测试的规则')
      return
    }

    // Fact 对象配置模式
    const validConfigs = ruleConfigs.value.filter(config =>
      config.className.trim() &&
      config.fields.some(field => field.key.trim() && field.value.trim())
    )

    if (validConfigs.length === 0) {
      message.error('请配置至少一个有效的 Fact 对象')
      return
    }

    // 构建 RuleFactConfig 数组
    const ruleFactConfigs = validConfigs.map(config => ({
      factName: `$fact${Date.now()}`, // 自动生成变量名
      className: config.className,
      fieldValues: Object.fromEntries(
        config.fields
          .filter(field => field.key.trim() && field.value.trim())
          .map(field => [field.key, field.value])
      )
    }))

    const testRequest: TestRuleRequest = {
      ruleConfigs: ruleFactConfigs,
      ruleIds: selectedRuleIds.value,
      verbose: testOptions.value.showExecutionDetails
    }

    // 执行测试
    const result = await withLoading(() => ruleService.testRule(testRequest))
    testResult.value = result

    if (result.success) {
      message.success('测试执行成功')
    } else {
      message.error('测试执行失败')
    }



  } catch (err) {
    console.error('执行测试失败:', err)
    error.value = '执行测试失败，请稍后重试'
    message.error('执行测试失败')
  }
}

const handleClearAll = () => {
  selectedRuleIds.value = []
  ruleConfigs.value = [
    {
      className: '',
      fields: [{ key: '', value: '' }]
    }
  ]
  testResult.value = null
  clearError()
}

const handleClearResult = () => {
  testResult.value = null
}

const handleRuleSelectionChange = () => {
  // 规则选择变化时的处理
}

// RuleFactConfig 相关方法
const addFactConfig = () => {
  ruleConfigs.value.push({
    className: '',
    fields: [{ key: '', value: '' }]
  })
}

const removeFactConfig = (index: number) => {
  if (ruleConfigs.value.length > 1) {
    ruleConfigs.value.splice(index, 1)
  }
}

const addFieldValue = (configIndex: number) => {
  ruleConfigs.value[configIndex].fields.push({ key: '', value: '' })
}

const removeFieldValue = (configIndex: number, fieldIndex: number) => {
  if (ruleConfigs.value[configIndex].fields.length > 1) {
    ruleConfigs.value[configIndex].fields.splice(fieldIndex, 1)
  }
}

const handleExportResult = () => {
  if (!testResult.value) return

  const dataStr = JSON.stringify(testResult.value, null, 2)
  const dataBlob = new Blob([dataStr], { type: 'application/json' })
  const url = URL.createObjectURL(dataBlob)
  const link = document.createElement('a')
  link.href = url
  link.download = `test-result-${Date.now()}.json`
  link.click()
  URL.revokeObjectURL(url)

  message.success('测试结果已导出')
}

const viewRuleDetails = (rule: any) => {
  // 查看规则详情的处理
  message.info(`查看规则详情: ${rule.ruleName}`)
}



const clearError = () => {
  error.value = null
}

// 页面挂载时加载数据
onMounted(() => {
  loadRules()
})
</script>

<style scoped>
.rule-test {
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

.test-content {
  margin-bottom: 32px;
}

.test-config-section,
.test-result-section {
  background: #fafafa;
  border-radius: 6px;
  padding: 16px;
  height: fit-content;
}

.section-title {
  margin: 0 0 16px 0;
  font-size: 16px;
  font-weight: 500;
  color: #262626;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.config-item {
  margin-bottom: 24px;
}

.config-label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  color: #262626;
}

.config-help {
  margin-top: 4px;
  font-size: 12px;
  color: #8c8c8c;
}

.form-input-section {
  padding: 16px;
  background: white;
  border-radius: 4px;
  border: 1px solid #d9d9d9;
}

.dynamic-fields {
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  padding: 12px;
  background: #fafafa;
}

.field-row {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
}

.field-row:last-child {
  margin-bottom: 0;
}

.result-content {
  min-height: 400px;
}

.empty-result {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 300px;
}

.empty-tip {
  margin-top: 8px;
  color: #8c8c8c;
  font-size: 12px;
}

.loading-result {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 300px;
}

.loading-placeholder {
  width: 100%;
  height: 200px;
}

.result-display>div {
  margin-bottom: 16px;
}

.result-summary {
  padding: 16px;
  background: white;
  border-radius: 4px;
  border: 1px solid #d9d9d9;
}

.fired-rules h4,
.result-data h4,
.execution-details h4 {
  margin-bottom: 8px;
  color: #262626;
  font-size: 14px;
  font-weight: 500;
}

.execution-logs {
  background: #f5f5f5;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  padding: 12px;
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
  font-size: 12px;
  line-height: 1.5;
  white-space: pre-wrap;
  max-height: 200px;
  overflow-y: auto;
}



.error-alert {
  margin-top: 16px;
}

/* Fact 配置相关样式 */
.fact-config-section {
  padding: 16px;
  background: white;
  border-radius: 4px;
  border: 1px solid #d9d9d9;
}

.fact-config-item {
  border: 1px solid #e8e8e8;
  border-radius: 6px;
  padding: 16px;
  margin-bottom: 16px;
  background: #fafafa;
}

.fact-config-item:last-child {
  margin-bottom: 0;
}

.fact-config-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.fact-config-header h4 {
  margin: 0;
  color: #262626;
  font-size: 14px;
  font-weight: 500;
}

.field-values {
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  padding: 12px;
  background: white;
}

.field-value-row {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
}

.field-value-row:last-child {
  margin-bottom: 0;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .test-content .ant-row .ant-col:first-child {
    margin-bottom: 16px;
  }
}

@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    align-items: stretch;
  }

  .header-actions {
    margin-top: 16px;
  }

  .section-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .result-actions,
  .history-actions {
    margin-top: 8px;
  }

  .field-row,
  .field-value-row {
    flex-direction: column;
    align-items: stretch;
  }

  .field-row input,
  .field-value-row input {
    width: 100% !important;
    margin: 4px 0 !important;
  }

  .fact-config-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .fact-config-header button {
    margin-top: 8px;
  }
}

/* 历史详情 JSON 显示样式 */
.history-json-display {
  background: #f5f5f5;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  padding: 12px;
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
  font-size: 12px;
  line-height: 1.5;
  white-space: pre-wrap;
  max-height: 200px;
  overflow-y: auto;
  margin: 0;
}
</style>