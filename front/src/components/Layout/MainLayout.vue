<template>
  <a-layout class="main-layout">
    <a-layout-header class="header">
      <div class="logo">
        <h2>Drools 动态规则管理系统</h2>
      </div>
      <a-menu
        v-model:selectedKeys="selectedKeys"
        mode="horizontal"
        theme="dark"
        class="menu"
        @click="handleMenuClick"
      >
        <a-menu-item key="rules">
          <template #icon>
            <UnorderedListOutlined />
          </template>
          规则管理
        </a-menu-item>
        <a-menu-item key="test">
          <template #icon>
            <ExperimentOutlined />
          </template>
          规则测试
        </a-menu-item>
      </a-menu>
    </a-layout-header>
    
    <a-layout-content class="content">
      <div class="content-wrapper">
        <slot />
      </div>
    </a-layout-content>
    
    <a-layout-footer class="footer">
      Drools Dynamic Rules Management System ©2024
    </a-layout-footer>
  </a-layout>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { UnorderedListOutlined, ExperimentOutlined } from '@ant-design/icons-vue'

const router = useRouter()
const route = useRoute()

const selectedKeys = ref<string[]>([])

// 根据当前路由设置选中的菜单项
const updateSelectedKeys = () => {
  const path = route.path
  if (path.startsWith('/rules/test')) {
    selectedKeys.value = ['test']
  } else if (path.startsWith('/rules')) {
    selectedKeys.value = ['rules']
  }
}

// 监听路由变化
watch(() => route.path, updateSelectedKeys, { immediate: true })

// 处理菜单点击
const handleMenuClick = ({ key }: { key: string }) => {
  switch (key) {
    case 'rules':
      router.push('/rules')
      break
    case 'test':
      router.push('/rules/test')
      break
  }
}
</script>

<style scoped>
.main-layout {
  min-height: 100vh;
}

.header {
  display: flex;
  align-items: center;
  padding: 0 24px;
  background: #001529;
}

.logo {
  margin-right: 24px;
}

.logo h2 {
  color: white;
  margin: 0;
  font-size: 18px;
  font-weight: 500;
}

.menu {
  flex: 1;
  border: none;
}

.content {
  background: #f0f2f5;
  min-height: calc(100vh - 64px - 70px);
}

.content-wrapper {
  margin: 24px;
  padding: 24px;
  background: white;
  border-radius: 6px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.12), 0 1px 2px rgba(0, 0, 0, 0.24);
  min-height: calc(100vh - 64px - 70px - 48px - 48px);
}

.footer {
  text-align: center;
  background: #f0f2f5;
  color: rgba(0, 0, 0, 0.65);
}
</style>