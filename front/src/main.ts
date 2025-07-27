import { createApp } from 'vue'
import { createPinia } from 'pinia'
import Antd from 'ant-design-vue'
import 'ant-design-vue/dist/reset.css'

import App from './App.vue'
import router from './router'

// 配置Monaco Editor环境
declare global {
  interface Window {
    MonacoEnvironment: any
  }
}

window.MonacoEnvironment = {
  getWorkerUrl: function (moduleId: string, label: string) {
    if (label === 'json') {
      return '/monaco-editor/min/vs/language/json/json.worker.js'
    }
    if (label === 'css' || label === 'scss' || label === 'less') {
      return '/monaco-editor/min/vs/language/css/css.worker.js'
    }
    if (label === 'html' || label === 'handlebars' || label === 'razor') {
      return '/monaco-editor/min/vs/language/html/html.worker.js'
    }
    if (label === 'typescript' || label === 'javascript') {
      return '/monaco-editor/min/vs/language/typescript/ts.worker.js'
    }
    return '/monaco-editor/min/vs/editor/editor.worker.js'
  }
}

const app = createApp(App)

app.use(createPinia())
app.use(router)
app.use(Antd)

app.mount('#app')