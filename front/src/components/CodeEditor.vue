<template>
  <div class="code-editor-container">
    <div class="editor-toolbar" v-if="showToolbar">
      <a-space>
        <a-button type="primary" size="small" @click="handleSave" :disabled="readonly" :loading="validating">
          <template #icon>
            <SaveOutlined />
          </template>
          保存
        </a-button>
        <a-button size="small" @click="handleFormat" :disabled="readonly">
          <template #icon>
            <FormatPainterOutlined />
          </template>
          格式化
        </a-button>
        <a-button size="small" @click="handleValidate" :loading="validating">
          <template #icon>
            <CheckCircleOutlined />
          </template>
          验证语法
        </a-button>
        <a-divider type="vertical" />
        <a-typography-text type="secondary" style="font-size: 12px;">
          {{ language.toUpperCase() }} | 行: {{ cursorPosition.line }} 列: {{ cursorPosition.column }}
        </a-typography-text>
      </a-space>
    </div>

    <!-- 使用带语法高亮的编辑器 -->
    <div class="editor-wrapper" :style="{ height: editorHeight }">
      <!-- 行号显示 -->
      <div class="line-numbers" ref="lineNumbersRef">
        <div v-for="lineNum in lineCount" :key="lineNum" class="line-number"
          :class="{ active: lineNum === cursorPosition.line }">
          {{ lineNum }}
        </div>
      </div>

      <!-- 编辑区域容器 -->
      <div class="editor-content">
        <!-- 语法高亮层 -->
        <pre class="syntax-highlight" ref="highlightRef" v-html="highlightedCode"></pre>

        <!-- 透明的textarea用于输入 -->
        <textarea ref="textareaRef" v-model="internalValue" class="code-textarea" :readonly="readonly"
          :placeholder="placeholder" @input="handleInput" @keydown="handleKeydown" @scroll="handleScroll"
          @select="handleSelect" spellcheck="false" />
      </div>
    </div>

    <!-- 错误提示面板 -->
    <div v-if="validationErrors.length > 0" class="error-panel">
      <a-alert type="error" show-icon closable @close="clearErrors">
        <template #message>
          <div class="error-title">发现 {{ validationErrors.length }} 个语法错误</div>
        </template>
        <template #description>
          <div class="error-list">
            <div v-for="(error, index) in validationErrors" :key="index" class="error-item" @click="goToError(error)">
              <a-typography-text type="danger">
                第 {{ error.line }} 行: {{ error.message }}
              </a-typography-text>
            </div>
          </div>
        </template>
      </a-alert>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, nextTick } from 'vue'
import { message } from 'ant-design-vue'
import {
  SaveOutlined,
  FormatPainterOutlined,
  CheckCircleOutlined
} from '@ant-design/icons-vue'
import type { CodeEditorProps, CodeEditorEmits } from '@/types/components'
import { ruleService } from '@/services'

// 定义组件属性
const props = withDefaults(defineProps<CodeEditorProps>(), {
  language: 'drools',
  theme: 'vs',
  height: '400px',
  width: '100%',
  readonly: false,
  showToolbar: true,
  placeholder: '请输入Drools规则内容...',
  options: () => ({})
})

// 定义组件事件
const emit = defineEmits<CodeEditorEmits>()

// 响应式数据
const textareaRef = ref<HTMLTextAreaElement>()
const lineNumbersRef = ref<HTMLElement>()
const highlightRef = ref<HTMLElement>()
const validating = ref(false)
const validationErrors = ref<Array<{ line: number; message: string }>>([])
const cursorPosition = ref({ line: 1, column: 1 })
const internalValue = ref(props.modelValue || '')

// 计算属性
const editorHeight = computed(() => {
  if (typeof props.height === 'number') {
    return `${props.height}px`
  }
  return props.height
})

const showToolbar = computed(() => props.showToolbar !== false)

const lineCount = computed(() => {
  return Math.max(1, internalValue.value.split('\n').length)
})

const placeholder = computed(() => {
  return props.placeholder || '请输入代码...'
})

// Drools DRL 语法高亮
const highlightedCode = computed(() => {
  if (!internalValue.value) return ''

  return highlightDroolsCode(internalValue.value)
})

// Drools 语法高亮函数
const highlightDroolsCode = (code: string): string => {
  // Drools 关键字
  const keywords = [
    'rule', 'when', 'then', 'end', 'package', 'import', 'global', 'function',
    'query', 'declare', 'template', 'dialect', 'salience', 'no-loop', 'ruleflow-group',
    'agenda-group', 'auto-focus', 'lock-on-active', 'date-effective', 'date-expires',
    'enabled', 'duration', 'timer', 'calendars', 'activation-group'
  ]

  // 操作符
  const operators = [
    'and', 'or', 'not', 'exists', 'forall', 'from', 'collect', 'accumulate',
    'eval', 'matches', 'contains', 'memberOf', 'soundslike', 'str'
  ]

  let highlightedCode = code

  // 转义 HTML 特殊字符
  highlightedCode = highlightedCode
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#39;')

  // 高亮注释
  highlightedCode = highlightedCode
    .replace(/(\/\/.*$)/gm, '<span class="comment">$1</span>')
    .replace(/(\/\*[\s\S]*?\*\/)/g, '<span class="comment">$1</span>')

  // 高亮字符串
  highlightedCode = highlightedCode
    .replace(/("(?:[^"\\]|\\.)*")/g, '<span class="string">$1</span>')
    .replace(/('(?:[^'\\]|\\.)*')/g, '<span class="string">$1</span>')

  // 高亮数字
  highlightedCode = highlightedCode
    .replace(/\b(\d+\.?\d*)\b/g, '<span class="number">$1</span>')

  // 高亮关键字
  keywords.forEach(keyword => {
    const regex = new RegExp('\\b(' + keyword + ')\\b', 'gi')
    highlightedCode = highlightedCode.replace(regex, '<span class="keyword">$1</span>')
  })

  // 高亮操作符
  operators.forEach(operator => {
    const regex = new RegExp('\\b(' + operator + ')\\b', 'gi')
    highlightedCode = highlightedCode.replace(regex, '<span class="operator">$1</span>')
  })

  // 高亮比较操作符
  highlightedCode = highlightedCode
    .replace(/(==)/g, '<span class="comparison">$1</span>')
    .replace(/(!=)/g, '<span class="comparison">$1</span>')
    .replace(/(<=)/g, '<span class="comparison">$1</span>')
    .replace(/(>=)/g, '<span class="comparison">$1</span>')
    .replace(/([^<>=!])&lt;([^=])/g, '$1<span class="comparison">&lt;</span>$2')
    .replace(/([^<>=!])&gt;([^=])/g, '$1<span class="comparison">&gt;</span>$2')
    .replace(/(=~)/g, '<span class="comparison">$1</span>')
    .replace(/(!~)/g, '<span class="comparison">$1</span>')

  // 高亮变量绑定 ($variable)
  highlightedCode = highlightedCode
    .replace(/(\$\w+)/g, '<span class="variable">$1</span>')

  // 高亮类型和包名
  highlightedCode = highlightedCode
    .replace(/\b([A-Z][a-zA-Z0-9]*(?:\.[A-Z][a-zA-Z0-9]*)*)\b/g, '<span class="type">$1</span>')

  return highlightedCode
}

// 处理输入
const handleInput = (event: Event) => {
  const target = event.target as HTMLTextAreaElement
  internalValue.value = target.value
  emit('update:modelValue', target.value)
  emit('change', target.value)
  updateCursorPosition()
  syncLineNumbers()
  syncHighlight()
}

// 处理键盘事件
const handleKeydown = (event: KeyboardEvent) => {
  const target = event.target as HTMLTextAreaElement

  // Tab键处理
  if (event.key === 'Tab') {
    event.preventDefault()
    const start = target.selectionStart
    const end = target.selectionEnd
    const value = target.value

    if (event.shiftKey) {
      // Shift+Tab: 减少缩进
      const lineStart = value.lastIndexOf('\n', start - 1) + 1
      const lineEnd = value.indexOf('\n', start)
      const line = value.substring(lineStart, lineEnd === -1 ? value.length : lineEnd)

      if (line.startsWith('  ')) {
        target.value = value.substring(0, lineStart) + line.substring(2) + value.substring(lineEnd === -1 ? value.length : lineEnd)
        target.selectionStart = target.selectionEnd = start - 2
        handleInput(event)
      }
    } else {
      // Tab: 增加缩进
      target.value = value.substring(0, start) + '  ' + value.substring(end)
      target.selectionStart = target.selectionEnd = start + 2
      handleInput(event)
    }
  }

  // Ctrl+S: 保存
  if (event.ctrlKey && event.key === 's') {
    event.preventDefault()
    handleSave()
  }

  // 更新光标位置
  nextTick(() => {
    updateCursorPosition()
  })
}

// 处理滚动同步
const handleScroll = () => {
  if (textareaRef.value && lineNumbersRef.value && highlightRef.value) {
    const scrollTop = textareaRef.value.scrollTop
    const scrollLeft = textareaRef.value.scrollLeft

    lineNumbersRef.value.scrollTop = scrollTop
    highlightRef.value.scrollTop = scrollTop
    highlightRef.value.scrollLeft = scrollLeft
  }
}

// 处理选择变化
const handleSelect = () => {
  updateCursorPosition()
}

// 更新光标位置
const updateCursorPosition = () => {
  if (!textareaRef.value) return

  const textarea = textareaRef.value
  const cursorPos = textarea.selectionStart
  const textBeforeCursor = textarea.value.substring(0, cursorPos)
  const lines = textBeforeCursor.split('\n')

  cursorPosition.value = {
    line: lines.length,
    column: lines[lines.length - 1].length + 1
  }
}

// 同步行号显示
const syncLineNumbers = () => {
  nextTick(() => {
    if (textareaRef.value && lineNumbersRef.value) {
      lineNumbersRef.value.scrollTop = textareaRef.value.scrollTop
    }
  })
}

// 同步语法高亮
const syncHighlight = () => {
  nextTick(() => {
    if (textareaRef.value && highlightRef.value) {
      highlightRef.value.scrollTop = textareaRef.value.scrollTop
      highlightRef.value.scrollLeft = textareaRef.value.scrollLeft
    }
  })
}

// 处理保存
const handleSave = async () => {
  if (props.readonly) return

  const content = internalValue.value

  // 先验证语法
  await handleValidate()

  if (validationErrors.value.length === 0) {
    emit('save', content)
    if (props.onSave) {
      props.onSave(content)
    }
    message.success('保存成功')
  } else {
    message.error('请先修复语法错误')
  }
}

// 处理格式化
const handleFormat = async () => {
  if (props.readonly) return

  try {
    // 简单的格式化逻辑
    const lines = internalValue.value.split('\n')
    let indentLevel = 0
    const formattedLines = lines.map(line => {
      const trimmedLine = line.trim()

      if (trimmedLine === '') return ''

      // 减少缩进的关键字
      if (trimmedLine.startsWith('end') || trimmedLine.startsWith('}')) {
        indentLevel = Math.max(0, indentLevel - 1)
      }

      const formattedLine = '  '.repeat(indentLevel) + trimmedLine

      // 增加缩进的关键字
      if (trimmedLine.startsWith('rule') ||
        trimmedLine.startsWith('when') ||
        trimmedLine.startsWith('then') ||
        trimmedLine.endsWith('{')) {
        indentLevel++
      }

      return formattedLine
    })

    internalValue.value = formattedLines.join('\n')
    emit('update:modelValue', internalValue.value)
    emit('change', internalValue.value)
    message.success('格式化完成')
  } catch (error) {
    console.warn('格式化失败:', error)
    message.warning('格式化失败')
  }
}

// 处理语法验证
const handleValidate = async () => {
  validating.value = true

  try {
    if (!internalValue.value.trim()) {
      message.error('请输入规则内容')
      return
    }

    // 调用后端接口验证规则语法
    const validationResult = await ruleService.validateRule(internalValue.value)

    if (validationResult.valid) {
      validationErrors.value = []
      message.success('语法验证通过')
    } else {
      // 将后端返回的错误信息转换为前端格式
      const errors = [{
        line: 1,
        message: validationResult.errorMessage || '语法验证失败'
      }]

      validationErrors.value = errors
      message.error(`语法验证失败`)
    }

  } catch (error: any) {
    console.error('语法验证失败:', error)
    const errorMessage = error.message || '语法验证失败'
    message.error(errorMessage)

    // 设置错误信息
    validationErrors.value = [{
      line: 1,
      message: errorMessage
    }]
  } finally {
    validating.value = false
  }
}

// 跳转到错误位置
const goToError = (error: { line: number; message: string }) => {
  if (!textareaRef.value) return

  const textarea = textareaRef.value
  const lines = textarea.value.split('\n')
  let position = 0

  for (let i = 0; i < error.line - 1 && i < lines.length; i++) {
    position += lines[i].length + 1 // +1 for newline
  }

  textarea.focus()
  textarea.setSelectionRange(position, position + (lines[error.line - 1]?.length || 0))
  textarea.scrollIntoView({ behavior: 'smooth', block: 'center' })
}

// 清除错误
const clearErrors = () => {
  validationErrors.value = []
}

// 监听属性变化
watch(() => props.modelValue, (newValue) => {
  if (internalValue.value !== newValue) {
    internalValue.value = newValue || ''
    nextTick(() => {
      syncLineNumbers()
      syncHighlight()
    })
  }
})

// 暴露方法给父组件
defineExpose({
  getValue: () => internalValue.value,
  setValue: (value: string) => {
    internalValue.value = value
    emit('update:modelValue', value)
    emit('change', value)
  },
  focus: () => textareaRef.value?.focus(),
  format: handleFormat,
  validate: handleValidate,
  insertText: (text: string) => {
    if (textareaRef.value) {
      const start = textareaRef.value.selectionStart
      const end = textareaRef.value.selectionEnd
      const value = textareaRef.value.value

      textareaRef.value.value = value.substring(0, start) + text + value.substring(end)
      textareaRef.value.selectionStart = textareaRef.value.selectionEnd = start + text.length

      internalValue.value = textareaRef.value.value
      emit('update:modelValue', internalValue.value)
      emit('change', internalValue.value)
    }
  }
})
</script>

<style scoped>
.code-editor-container {
  border: 1px solid #d9d9d9;
  border-radius: 6px;
  overflow: hidden;
  background-color: #fff;
}

.editor-toolbar {
  padding: 8px 12px;
  background-color: #fafafa;
  border-bottom: 1px solid #d9d9d9;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.editor-wrapper {
  position: relative;
  display: flex;
  background-color: #fff;
}

.editor-content {
  position: relative;
  flex: 1;
}

.line-numbers {
  width: 50px;
  background-color: #f8f8f8;
  border-right: 1px solid #e8e8e8;
  padding: 8px 4px;
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
  font-size: 12px;
  line-height: 20px;
  color: #999;
  text-align: right;
  user-select: none;
  overflow: hidden;
  flex-shrink: 0;
}

.line-number {
  height: 20px;
  padding-right: 8px;
  white-space: nowrap;
}

.line-number.active {
  color: #1890ff;
  font-weight: 500;
}

.syntax-highlight {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  padding: 8px 12px;
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
  font-size: 14px;
  line-height: 20px;
  white-space: pre;
  overflow: hidden;
  pointer-events: none;
  z-index: 1;
  background-color: transparent;
  margin: 0;
  border: none;
  font-variant-ligatures: none;
  font-feature-settings: normal;
  text-rendering: optimizeSpeed;
}

.code-textarea {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  border: none;
  outline: none;
  resize: none;
  padding: 8px 12px;
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
  font-size: 14px;
  line-height: 20px;
  background-color: transparent;
  color: transparent;
  caret-color: #262626;
  white-space: pre;
  margin: 0;
  overflow-wrap: normal;
  overflow-x: auto;
  overflow-y: auto;
  tab-size: 2;
  z-index: 2;
  font-variant-ligatures: none;
  font-feature-settings: normal;
  text-rendering: optimizeSpeed;
}

.code-textarea:focus {
  outline: none;
}

.code-textarea::placeholder {
  color: #bfbfbf;
  font-style: italic;
  opacity: 1;
}

.error-panel {
  border-top: 1px solid #d9d9d9;
  background-color: #fff;
}

.error-title {
  font-weight: 500;
  margin-bottom: 8px;
}

.error-list {
  max-height: 120px;
  overflow-y: auto;
}

.error-item {
  padding: 4px 0;
  cursor: pointer;
  transition: background-color 0.2s;
}

.error-item:hover {
  background-color: #f5f5f5;
  border-radius: 4px;
  padding-left: 8px;
  padding-right: 8px;
}

/* Drools 语法高亮样式 */
.syntax-highlight :deep(.keyword) {
  color: #0000ff;
}

.syntax-highlight :deep(.operator) {
  color: #800080;
}

.syntax-highlight :deep(.comparison) {
  color: #ff6600;
}

.syntax-highlight :deep(.string) {
  color: #008000;
}

.syntax-highlight :deep(.number) {
  color: #ff0000;
}

.syntax-highlight :deep(.comment) {
  color: #808080;
}

.syntax-highlight :deep(.variable) {
  color: #0080ff;
}

.syntax-highlight :deep(.type) {
  color: #2b91af;
}

/* 深色主题适配 */
.code-editor-container.dark {
  border-color: #434343;
  background-color: #1e1e1e;
}

.code-editor-container.dark .editor-toolbar {
  background-color: #252526;
  border-bottom-color: #434343;
  color: #cccccc;
}

.code-editor-container.dark .editor-wrapper {
  background-color: #1e1e1e;
}

.code-editor-container.dark .line-numbers {
  background-color: #252526;
  border-right-color: #434343;
  color: #858585;
}

.code-editor-container.dark .line-number.active {
  color: #569cd6;
}

.code-editor-container.dark .code-textarea {
  background-color: #1e1e1e;
  color: #d4d4d4;
}

.code-editor-container.dark .code-textarea::placeholder {
  color: #6a6a6a;
}

.code-editor-container.dark .error-panel {
  background-color: #1e1e1e;
  border-top-color: #434343;
}

/* 深色主题语法高亮 */
.code-editor-container.dark .syntax-highlight :deep(.keyword) {
  color: #569cd6;
}

.code-editor-container.dark .syntax-highlight :deep(.operator) {
  color: #c586c0;
}

.code-editor-container.dark .syntax-highlight :deep(.comparison) {
  color: #d4d4d4;
}

.code-editor-container.dark .syntax-highlight :deep(.string) {
  color: #ce9178;
}

.code-editor-container.dark .syntax-highlight :deep(.number) {
  color: #b5cea8;
}

.code-editor-container.dark .syntax-highlight :deep(.comment) {
  color: #6a9955;
}

.code-editor-container.dark .syntax-highlight :deep(.variable) {
  color: #9cdcfe;
}

.code-editor-container.dark .syntax-highlight :deep(.type) {
  color: #4ec9b0;
}

/* 滚动条样式 */
.code-textarea::-webkit-scrollbar,
.line-numbers::-webkit-scrollbar,
.error-list::-webkit-scrollbar,
.syntax-highlight::-webkit-scrollbar {
  width: 8px;
  height: 8px;
}

.code-textarea::-webkit-scrollbar-track,
.line-numbers::-webkit-scrollbar-track,
.error-list::-webkit-scrollbar-track,
.syntax-highlight::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 4px;
}

.code-textarea::-webkit-scrollbar-thumb,
.line-numbers::-webkit-scrollbar-thumb,
.error-list::-webkit-scrollbar-thumb,
.syntax-highlight::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 4px;
}

.code-textarea::-webkit-scrollbar-thumb:hover,
.line-numbers::-webkit-scrollbar-thumb:hover,
.error-list::-webkit-scrollbar-thumb:hover,
.syntax-highlight::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

/* 深色主题滚动条 */
.code-editor-container.dark .code-textarea::-webkit-scrollbar-track,
.code-editor-container.dark .line-numbers::-webkit-scrollbar-track,
.code-editor-container.dark .error-list::-webkit-scrollbar-track,
.code-editor-container.dark .syntax-highlight::-webkit-scrollbar-track {
  background: #2d2d30;
}

.code-editor-container.dark .code-textarea::-webkit-scrollbar-thumb,
.code-editor-container.dark .line-numbers::-webkit-scrollbar-thumb,
.code-editor-container.dark .error-list::-webkit-scrollbar-thumb,
.code-editor-container.dark .syntax-highlight::-webkit-scrollbar-thumb {
  background: #424242;
}

.code-editor-container.dark .code-textarea::-webkit-scrollbar-thumb:hover,
.code-editor-container.dark .line-numbers::-webkit-scrollbar-thumb:hover,
.code-editor-container.dark .error-list::-webkit-scrollbar-thumb:hover,
.code-editor-container.dark .syntax-highlight::-webkit-scrollbar-thumb:hover {
  background: #4f4f4f;
}
</style>