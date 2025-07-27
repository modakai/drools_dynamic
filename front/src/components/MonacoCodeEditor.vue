<template>
  <div class="monaco-code-editor-container">
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
          DRL | 行: {{ cursorPosition.line }} 列: {{ cursorPosition.column }}
        </a-typography-text>
      </a-space>
    </div>

    <!-- Monaco Editor 容器 -->
    <div ref="editorContainer" class="monaco-editor-wrapper" :style="{ height: editorHeight }"></div>

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
import { ref, computed, watch, nextTick, onMounted, onBeforeUnmount } from 'vue'
import { message } from 'ant-design-vue'
import * as monaco from 'monaco-editor'
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
const editorContainer = ref<HTMLElement>()
const validating = ref(false)
const validationErrors = ref<Array<{ line: number; message: string }>>([])
const cursorPosition = ref({ line: 1, column: 1 })
const internalValue = ref(props.modelValue || '')

let editor: monaco.editor.IStandaloneCodeEditor | null = null

// 计算属性
const editorHeight = computed(() => {
  if (typeof props.height === 'number') {
    return `${props.height}px`
  }
  return props.height
})

const showToolbar = computed(() => props.showToolbar !== false)

// Drools DRL 关键字列表
const droolsKeywords = [
  "package", "import", "global", "dialect", "rule", "when", "then", "end",
  "if", "else", "while", "do", "for", "foreach", "function", "query",
  "declare", "extends", "new", "this", "null", "true", "false",
  "eval", "not", "exists", "forall", "from", "collect", "accumulate",
  "action", "agenda-group", "auto-focus", "no-loop", "duration", "timer",
  "date-effective", "date-expires", "lock-on-active", "activation-group",
  "ruleflow-group", "entry-point", "matches", "soundslike", "distance",
  "contains", "excludes", "memberof", "over", "init", "result", "reverse",
  "cancel", "call", "break", "salience", "enabled", "calendars"
]

// 注册 Drools DRL 语言
const registerDroolsLanguage = () => {
  // Step 1: 注册语言
  monaco.languages.register({ id: 'drools' })

  // Step 2: 添加语法高亮
  monaco.languages.setMonarchTokensProvider('drools', {
    keywords: droolsKeywords,
    
    // 操作符
    operators: [
      '==', '!=', '<', '>', '<=', '>=', '=~', '!~',
      '&&', '||', '!', '+', '-', '*', '/', '%',
      '=', '+=', '-=', '*=', '/=', '%='
    ],

    // 符号
    symbols: /[=><!~?:&|+\-*\/\^%]+/,

    tokenizer: {
      root: [
        // 标识符和关键字
        [/[a-zA-Z_$][\w$]*/, {
          cases: {
            '@keywords': 'keyword',
            '@default': 'identifier'
          }
        }],

        // 变量绑定 ($variable)
        [/\$[a-zA-Z_][\w]*/, 'variable'],

        // 数字
        [/\d*\.\d+([eE][\-+]?\d+)?/, 'number.float'],
        [/\d+/, 'number'],

        // 字符串
        [/"([^"\\]|\\.)*$/, 'string.invalid'],  // 未闭合的字符串
        [/"/, 'string', '@string_double'],
        [/'([^'\\]|\\.)*$/, 'string.invalid'],  // 未闭合的字符串
        [/'/, 'string', '@string_single'],

        // 注释
        [/\/\*/, 'comment', '@comment'],
        [/\/\/.*$/, 'comment'],

        // 操作符
        [/@symbols/, {
          cases: {
            '@operators': 'operator',
            '@default': ''
          }
        }],

        // 分隔符
        [/[{}()\[\]]/, '@brackets'],
        [/[<>](?!@symbols)/, '@brackets'],
        [/[,.]/, 'delimiter'],

        // 空白字符
        [/[ \t\r\n]+/, 'white'],
      ],

      // 双引号字符串
      string_double: [
        [/[^\\"]+/, 'string'],
        [/\\./, 'string.escape.invalid'],
        [/"/, 'string', '@pop']
      ],

      // 单引号字符串
      string_single: [
        [/[^\\']+/, 'string'],
        [/\\./, 'string.escape.invalid'],
        [/'/, 'string', '@pop']
      ],

      // 多行注释
      comment: [
        [/[^\/*]+/, 'comment'],
        [/\/\*/, 'comment', '@push'],
        [/\*\//, 'comment', '@pop'],
        [/[\/*]/, 'comment']
      ],
    },
  })

  // Step 3: 定义主题
  monaco.editor.defineTheme('drools-theme', {
    base: 'vs',
    inherit: true,
    rules: [
      { token: 'keyword', foreground: '0000FF', fontStyle: 'bold' },
      { token: 'identifier', foreground: '000000' },
      { token: 'variable', foreground: '0080FF', fontStyle: 'bold' },
      { token: 'number', foreground: 'FF0000' },
      { token: 'number.float', foreground: 'FF0000' },
      { token: 'string', foreground: '008000' },
      { token: 'string.invalid', foreground: 'FF0000', fontStyle: 'italic' },
      { token: 'comment', foreground: '808080', fontStyle: 'italic' },
      { token: 'operator', foreground: 'FF6600', fontStyle: 'bold' },
      { token: 'delimiter', foreground: '000000' },
    ],
    colors: {
      'editor.background': '#FFFFFF',
      'editor.foreground': '#000000',
      'editorLineNumber.foreground': '#999999',
      'editorLineNumber.activeForeground': '#1890FF',
    }
  })

  // 深色主题
  monaco.editor.defineTheme('drools-theme-dark', {
    base: 'vs-dark',
    inherit: true,
    rules: [
      { token: 'keyword', foreground: '569CD6', fontStyle: 'bold' },
      { token: 'identifier', foreground: 'D4D4D4' },
      { token: 'variable', foreground: '9CDCFE', fontStyle: 'bold' },
      { token: 'number', foreground: 'B5CEA8' },
      { token: 'number.float', foreground: 'B5CEA8' },
      { token: 'string', foreground: 'CE9178' },
      { token: 'string.invalid', foreground: 'F44747', fontStyle: 'italic' },
      { token: 'comment', foreground: '6A9955', fontStyle: 'italic' },
      { token: 'operator', foreground: 'D4D4D4', fontStyle: 'bold' },
      { token: 'delimiter', foreground: 'D4D4D4' },
    ],
    colors: {
      'editor.background': '#1E1E1E',
      'editor.foreground': '#D4D4D4',
      'editorLineNumber.foreground': '#858585',
      'editorLineNumber.activeForeground': '#569CD6',
    }
  })

  // Step 4: 添加自动完成
  monaco.languages.registerCompletionItemProvider('drools', {
    provideCompletionItems: (model, position) => {
      const suggestions = [
        // 关键字自动完成
        ...droolsKeywords.map(keyword => ({
          label: keyword,
          kind: monaco.languages.CompletionItemKind.Keyword,
          insertText: keyword,
          documentation: `Drools关键字: ${keyword}`,
        })),

        // 常用代码片段
        {
          label: 'rule-template',
          kind: monaco.languages.CompletionItemKind.Snippet,
          insertText: [
            'rule "${1:规则名称}"',
            '    salience ${2:100}',
            '    when',
            '        ${3:// 条件}',
            '    then',
            '        ${4:// 动作}',
            'end'
          ].join('\n'),
          insertTextRules: monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
          documentation: '创建一个完整的规则模板',
        },

        {
          label: 'package-import',
          kind: monaco.languages.CompletionItemKind.Snippet,
          insertText: [
            'package ${1:com.example.rules}',
            '',
            'import ${2:com.example.domain.Entity}',
            ''
          ].join('\n'),
          insertTextRules: monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
          documentation: '添加包声明和导入语句',
        }
      ]

      return { suggestions }
    }
  })
}

// 初始化编辑器
const initEditor = () => {
  if (!editorContainer.value) return

  // 注册 Drools 语言支持
  registerDroolsLanguage()

  // 创建编辑器实例
  editor = monaco.editor.create(editorContainer.value, {
    value: internalValue.value,
    language: 'drools',
    theme: props.theme === 'dark' ? 'drools-theme-dark' : 'drools-theme',
    readOnly: props.readonly,
    fontSize: 14,
    fontFamily: 'Monaco, Menlo, "Ubuntu Mono", monospace',
    lineNumbers: 'on',
    minimap: { enabled: false },
    scrollBeyondLastLine: false,
    automaticLayout: true,
    tabSize: 2,
    insertSpaces: true,
    wordWrap: 'on',
    contextmenu: true,
    selectOnLineNumbers: true,
    roundedSelection: false,
    cursorStyle: 'line',
    ...props.options
  })

  // 监听内容变化
  editor.onDidChangeModelContent(() => {
    const value = editor?.getValue() || ''
    internalValue.value = value
    emit('update:modelValue', value)
    emit('change', value)
  })

  // 监听光标位置变化
  editor.onDidChangeCursorPosition((e) => {
    cursorPosition.value = {
      line: e.position.lineNumber,
      column: e.position.column
    }
  })

  // 监听焦点事件
  editor.onDidFocusEditorText(() => {
    // 编辑器获得焦点时的处理
  })
}

// 处理保存
const handleSave = async () => {
  if (props.readonly || !editor) return

  const content = editor.getValue()

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
  if (props.readonly || !editor) return

  try {
    await editor.getAction('editor.action.formatDocument')?.run()
    message.success('格式化完成')
  } catch (error) {
    console.warn('格式化失败:', error)
    message.warning('格式化失败')
  }
}

// 处理语法验证
const handleValidate = async () => {
  if (!editor) return

  validating.value = true

  try {
    const content = editor.getValue()
    
    if (!content.trim()) {
      message.error('请输入规则内容')
      return
    }

    // 调用后端接口验证规则语法
    const validationResult = await ruleService.validateRule(content)

    // 清除之前的错误标记
    monaco.editor.setModelMarkers(editor.getModel()!, 'drools', [])

    if (validationResult.valid) {
      validationErrors.value = []
      message.success('语法验证通过')
    } else {
      // 设置错误标记
      const markers: monaco.editor.IMarkerData[] = [{
        startLineNumber: 1,
        endLineNumber: 1,
        startColumn: 1,
        endColumn: 1,
        message: validationResult.errorMessage || '语法验证失败',
        severity: monaco.MarkerSeverity.Error,
      }]

      monaco.editor.setModelMarkers(editor.getModel()!, 'drools', markers)

      validationErrors.value = [{
        line: 1,
        message: validationResult.errorMessage || '语法验证失败'
      }]
      
      message.error('语法验证失败')
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
  if (!editor) return

  editor.setPosition({ lineNumber: error.line, column: 1 })
  editor.focus()
}

// 清除错误
const clearErrors = () => {
  validationErrors.value = []
  if (editor) {
    monaco.editor.setModelMarkers(editor.getModel()!, 'drools', [])
  }
}

// 监听属性变化
watch(() => props.modelValue, (newValue) => {
  if (editor && internalValue.value !== newValue) {
    internalValue.value = newValue || ''
    editor.setValue(newValue || '')
  }
})

watch(() => props.theme, (newTheme) => {
  if (editor) {
    monaco.editor.setTheme(newTheme === 'dark' ? 'drools-theme-dark' : 'drools-theme')
  }
})

// 生命周期
onMounted(() => {
  nextTick(() => {
    initEditor()
  })
})

onBeforeUnmount(() => {
  if (editor) {
    editor.dispose()
  }
})

// 暴露方法给父组件
defineExpose({
  getValue: () => editor?.getValue() || '',
  setValue: (value: string) => {
    if (editor) {
      editor.setValue(value)
      internalValue.value = value
      emit('update:modelValue', value)
      emit('change', value)
    }
  },
  focus: () => editor?.focus(),
  format: handleFormat,
  validate: handleValidate,
  insertText: (text: string) => {
    if (editor) {
      const selection = editor.getSelection()
      if (selection) {
        editor.executeEdits('', [{
          range: selection,
          text: text
        }])
      }
    }
  },
  getEditor: () => editor
})
</script>

<style scoped>
.monaco-code-editor-container {
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

.monaco-editor-wrapper {
  width: 100%;
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

/* 深色主题适配 */
.monaco-code-editor-container.dark {
  border-color: #434343;
  background-color: #1e1e1e;
}

.monaco-code-editor-container.dark .editor-toolbar {
  background-color: #252526;
  border-bottom-color: #434343;
  color: #cccccc;
}

.monaco-code-editor-container.dark .error-panel {
  background-color: #1e1e1e;
  border-top-color: #434343;
}
</style>