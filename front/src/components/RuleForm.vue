<template>
  <div class="rule-form-container">
    <a-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      layout="vertical"
      @finish="handleSubmit"
      @finishFailed="handleSubmitFailed"
    >
      <!-- 基本信息 -->
      <a-card title="基本信息" class="form-section">
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item
              label="规则名称"
              name="ruleName"
              :required="true"
            >
              <a-input
                v-model:value="formData.ruleName"
                placeholder="请输入规则名称"
                :disabled="readonly"
                :maxlength="100"
                show-count
              />
            </a-form-item>
          </a-col>
          
          <a-col :span="12">
            <a-form-item
              label="状态"
              name="enabled"
            >
              <a-switch
                v-model:checked="formData.enabled"
                :disabled="readonly"
                checked-children="启用"
                un-checked-children="禁用"
              />
            </a-form-item>
          </a-col>
        </a-row>
        
        <a-form-item
          label="描述"
          name="description"
        >
          <a-textarea
            v-model:value="formData.description"
            placeholder="请输入规则描述"
            :disabled="readonly"
            :maxlength="500"
            show-count
            :rows="3"
          />
        </a-form-item>
      </a-card>

      <!-- 规则内容 -->
      <a-card title="规则内容" class="form-section">
        <a-form-item
          label="Drools规则代码"
          name="ruleContent"
          :required="true"
        >
          <div class="code-editor-wrapper">
            <CodeEditor
              v-model:modelValue="formData.ruleContent"
              language="drools"
              :height="400"
              :readonly="readonly"
              :show-toolbar="!readonly"
              @save="handleCodeSave"
              @validate="handleCodeValidate"
            />
          </div>
          
          <!-- 语法提示 -->
          <div class="syntax-help" v-if="!readonly">
            <a-typography-title :level="5">语法提示：</a-typography-title>
            <a-typography-paragraph>
              <ul>
                <li><code>package</code> - 定义包名</li>
                <li><code>import</code> - 导入Java类</li>
                <li><code>rule "规则名" when ... then ... end</code> - 基本规则结构</li>
                <li><code>when</code> - 条件部分</li>
                <li><code>then</code> - 执行部分</li>
                <li><code>salience</code> - 规则优先级</li>
              </ul>
            </a-typography-paragraph>
          </div>
        </a-form-item>
      </a-card>

      <!-- 高级选项 -->
      <a-card title="高级选项" class="form-section" v-if="showAdvancedOptions">
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item
              label="版本号"
              name="version"
            >
              <a-input
                v-model:value="formData.version"
                placeholder="请输入版本号"
                :disabled="readonly || mode === 'edit'"
              />
            </a-form-item>
          </a-col>
          
          <a-col :span="12">
            <a-form-item
              label="规则组"
              name="ruleGroup"
            >
              <a-input
                v-model:value="formData.ruleGroup"
                placeholder="请输入规则组"
                :disabled="readonly"
              />
            </a-form-item>
          </a-col>
        </a-row>
        
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item
              label="优先级"
              name="salience"
            >
              <a-input-number
                v-model:value="formData.salience"
                placeholder="请输入优先级"
                :disabled="readonly"
                :min="0"
                :max="1000"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
          
          <a-col :span="12">
            <a-form-item
              label="生效时间"
              name="effectiveDate"
            >
              <a-date-picker
                v-model:value="formData.effectiveDate"
                placeholder="请选择生效时间"
                :disabled="readonly"
                style="width: 100%"
                show-time
              />
            </a-form-item>
          </a-col>
        </a-row>
      </a-card>

      <!-- 操作按钮 -->
      <div class="form-actions" v-if="!readonly">
        <a-space>
          <a-button
            type="primary"
            html-type="submit"
            :loading="loading"
            size="large"
          >
            <template #icon>
              <SaveOutlined />
            </template>
            {{ mode === 'create' ? '创建规则' : '更新规则' }}
          </a-button>
          
          <a-button
            @click="handleValidate"
            :loading="validating"
            size="large"
          >
            <template #icon>
              <CheckCircleOutlined />
            </template>
            验证规则
          </a-button>
          
          <a-button
            @click="handlePreview"
            size="large"
          >
            <template #icon>
              <EyeOutlined />
            </template>
            预览
          </a-button>
          
          <a-button
            @click="handleCancel"
            size="large"
          >
            <template #icon>
              <CloseOutlined />
            </template>
            取消
          </a-button>
        </a-space>
      </div>
    </a-form>

    <!-- 预览模态框 -->
    <a-modal
      v-model:open="previewVisible"
      title="规则预览"
      width="800px"
      :footer="null"
    >
      <div class="preview-content">
        <a-descriptions :column="2" bordered>
          <a-descriptions-item label="规则名称">
            {{ formData.ruleName }}
          </a-descriptions-item>
          <a-descriptions-item label="状态">
            <a-tag :color="formData.enabled ? 'success' : 'default'">
              {{ formData.enabled ? '启用' : '禁用' }}
            </a-tag>
          </a-descriptions-item>
          <a-descriptions-item label="版本号">
            {{ formData.version || '1.0' }}
          </a-descriptions-item>
          <a-descriptions-item label="规则组">
            {{ formData.ruleGroup || '-' }}
          </a-descriptions-item>
          <a-descriptions-item label="优先级">
            {{ formData.salience || 0 }}
          </a-descriptions-item>
          <a-descriptions-item label="生效时间">
            {{ formData.effectiveDate ? formatDate(formData.effectiveDate) : '-' }}
          </a-descriptions-item>
          <a-descriptions-item label="描述" :span="2">
            {{ formData.description || '-' }}
          </a-descriptions-item>
        </a-descriptions>
        
        <a-divider>规则内容</a-divider>
        
        <CodeEditor
          :model-value="formData.ruleContent"
          language="drools"
          :height="300"
          :readonly="true"
          :show-toolbar="false"
        />
      </div>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, watch, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import type { FormInstance, Rule } from 'ant-design-vue/es/form'
import {
  SaveOutlined,
  CheckCircleOutlined,
  EyeOutlined,
  CloseOutlined
} from '@ant-design/icons-vue'
import CodeEditor from './CodeEditor.vue'
import type { RuleFormProps, RuleFormEmits, RuleFormData } from '@/types/components'
import type { DroolsRule } from '@/types/rule'
import { formatDate } from '@/utils/date'

// 定义组件属性
const props = withDefaults(defineProps<RuleFormProps>(), {
  mode: 'create',
  loading: false,
  readonly: false
})

// 定义组件事件
const emit = defineEmits<RuleFormEmits>()

// 响应式数据
const formRef = ref<FormInstance>()
const validating = ref(false)
const previewVisible = ref(false)
const showAdvancedOptions = ref(false)

// 表单数据
const formData = reactive<RuleFormData & {
  ruleGroup?: string
  salience?: number
  effectiveDate?: string
}>({
  ruleName: '',
  ruleContent: '',
  description: '',
  enabled: true,
  version: '1.0',
  ruleGroup: '',
  salience: 0,
  effectiveDate: undefined
})

// 表单验证规则
const formRules: Record<string, Rule[]> = {
  ruleName: [
    { required: true, message: '请输入规则名称', trigger: 'blur' },
    { min: 2, max: 100, message: '规则名称长度应在2-100个字符之间', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9_\u4e00-\u9fa5]+$/, message: '规则名称只能包含字母、数字、下划线和中文', trigger: 'blur' }
  ],
  ruleContent: [
    { required: true, message: '请输入规则内容', trigger: 'blur' },
    { min: 10, message: '规则内容至少需要10个字符', trigger: 'blur' },
    {
      validator: async (rule: any, value: string) => {
        if (value && !value.includes('rule') && !value.includes('when') && !value.includes('then')) {
          throw new Error('规则内容应包含基本的Drools语法结构')
        }
      },
      trigger: 'blur'
    }
  ],
  description: [
    { max: 500, message: '描述长度不能超过500个字符', trigger: 'blur' }
  ],
  version: [
    { pattern: /^\d+\.\d+(\.\d+)?$/, message: '版本号格式应为x.y或x.y.z', trigger: 'blur' }
  ],
  salience: [
    { type: 'number', min: 0, max: 1000, message: '优先级应在0-1000之间', trigger: 'blur' }
  ]
}

// 计算属性
const isEditMode = computed(() => props.mode === 'edit')
const formTitle = computed(() => props.mode === 'create' ? '创建规则' : '编辑规则')

// 初始化表单数据
const initFormData = () => {
  if (props.rule) {
    Object.assign(formData, {
      ruleName: props.rule.ruleName || '',
      ruleContent: props.rule.ruleContent || '',
      description: props.rule.description || '',
      enabled: props.rule.enabled ?? true,
      version: props.rule.version || '1.0'
    })
  } else {
    // 重置为默认值
    Object.assign(formData, {
      ruleName: '',
      ruleContent: '',
      description: '',
      enabled: true,
      version: '1.0',
      ruleGroup: '',
      salience: 0,
      effectiveDate: undefined
    })
  }
}

// 事件处理函数
const handleSubmit = async () => {
  try {
    // 先验证表单
    await formRef.value?.validate()
    
    // 构建提交数据
    const submitData: DroolsRule = {
      ruleName: formData.ruleName,
      ruleContent: formData.ruleContent,
      description: formData.description,
      enabled: formData.enabled,
      version: formData.version
    }
    
    // 如果是编辑模式，添加ID
    if (isEditMode.value && props.rule?.id) {
      submitData.id = props.rule.id
    }
    
    emit('submit', submitData)
    message.success(`${formTitle.value}成功`)
    
  } catch (error) {
    console.error('表单验证失败:', error)
    message.error('请检查表单输入')
    emit('validate', false, ['表单验证失败'])
  }
}

const handleSubmitFailed = (errorInfo: any) => {
  console.error('表单提交失败:', errorInfo)
  const errors = errorInfo.errorFields?.map((field: any) => field.errors[0]) || []
  emit('validate', false, errors)
  message.error('请检查表单输入')
}

const handleValidate = async () => {
  validating.value = true
  
  try {
    // 验证表单
    await formRef.value?.validate()
    
    // 这里可以添加额外的规则语法验证
    if (formData.ruleContent) {
      // 简单的语法检查
      const hasRule = formData.ruleContent.includes('rule')
      const hasWhen = formData.ruleContent.includes('when')
      const hasThen = formData.ruleContent.includes('then')
      const hasEnd = formData.ruleContent.includes('end')
      
      if (!hasRule || !hasWhen || !hasThen || !hasEnd) {
        throw new Error('规则语法不完整，请检查是否包含rule、when、then、end关键字')
      }
    }
    
    emit('validate', true, [])
    message.success('规则验证通过')
    
  } catch (error: any) {
    const errorMessage = error.message || '规则验证失败'
    emit('validate', false, [errorMessage])
    message.error(errorMessage)
  } finally {
    validating.value = false
  }
}

const handlePreview = () => {
  previewVisible.value = true
}

const handleCancel = () => {
  emit('cancel')
}

const handleCodeSave = (content: string) => {
  formData.ruleContent = content
  message.success('代码已保存')
}

const handleCodeValidate = (markers: any[]) => {
  const errors = markers.filter(marker => marker.severity === 8) // Error severity
  if (errors.length > 0) {
    message.error(`代码中发现 ${errors.length} 个错误`)
  } else {
    message.success('代码语法检查通过')
  }
}

// 监听属性变化
watch(() => props.rule, () => {
  initFormData()
}, { immediate: true, deep: true })

// 生命周期
onMounted(() => {
  initFormData()
})

// 暴露方法给父组件
defineExpose({
  validate: () => formRef.value?.validate(),
  resetFields: () => formRef.value?.resetFields(),
  getFormData: () => formData,
  setFormData: (data: Partial<RuleFormData>) => Object.assign(formData, data)
})
</script>

<style scoped>
.rule-form-container {
  max-width: 1200px;
  margin: 0 auto;
}

.form-section {
  margin-bottom: 24px;
}

.form-section :deep(.ant-card-head) {
  background-color: #fafafa;
}

.code-editor-wrapper {
  border: 1px solid #d9d9d9;
  border-radius: 6px;
  overflow: hidden;
}

.syntax-help {
  margin-top: 16px;
  padding: 16px;
  background-color: #f6f8fa;
  border-radius: 6px;
  border: 1px solid #e1e4e8;
}

.syntax-help ul {
  margin: 0;
  padding-left: 20px;
}

.syntax-help li {
  margin-bottom: 4px;
}

.syntax-help code {
  background-color: #f1f3f4;
  padding: 2px 4px;
  border-radius: 3px;
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
}

.form-actions {
  text-align: center;
  padding: 24px 0;
  border-top: 1px solid #f0f0f0;
  margin-top: 24px;
}

.preview-content {
  max-height: 600px;
  overflow-y: auto;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .rule-form-container {
    padding: 0 16px;
  }
  
  .form-actions .ant-space {
    flex-direction: column;
    width: 100%;
  }
  
  .form-actions .ant-btn {
    width: 100%;
    margin-bottom: 8px;
  }
}

/* 表单样式优化 */
:deep(.ant-form-item-label > label) {
  font-weight: 600;
}

:deep(.ant-form-item-required::before) {
  color: #ff4d4f;
}

:deep(.ant-card-head-title) {
  font-weight: 600;
}

/* 代码编辑器样式 */
:deep(.code-editor-container) {
  border: none;
}

/* 开关样式 */
:deep(.ant-switch-checked) {
  background-color: #52c41a;
}

/* 输入框计数器样式 */
:deep(.ant-input-show-count-suffix) {
  color: #8c8c8c;
}

:deep(.ant-textarea-show-count::after) {
  color: #8c8c8c;
}
</style>