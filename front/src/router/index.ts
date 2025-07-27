import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    redirect: '/rules'
  },
  {
    path: '/rules',
    name: 'RuleManagement',
    component: () => import('@/views/RuleManagement.vue'),
    meta: {
      title: '规则管理'
    }
  },
  {
    path: '/rules/create',
    name: 'RuleCreate',
    component: () => import('@/views/RuleEditor.vue'),
    meta: {
      title: '创建规则',
      create_flag: true,
    }
  },
  {
    path: '/rules/:id/edit',
    name: 'RuleEdit',
    component: () => import('@/views/RuleEditor.vue'),
    meta: {
      title: '编辑规则',
      create_flag: false,
    }
  },
  {
    path: '/rules/test',
    name: 'RuleTest',
    component: () => import('@/views/RuleTest.vue'),
    meta: {
      title: '规则测试'
    }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, _from, next) => {
  if (to.meta?.title) {
    document.title = `${to.meta.title} - Drools 动态规则管理系统`
  }
  next()
})

export default router