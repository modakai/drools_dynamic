import { defineConfig } from 'vitepress'

export default defineConfig({
  title: 'Drools 动态规则管理系统',
  description: '基于 Drools 规则引擎的动态规则管理平台使用说明',
  lang: 'zh-CN',
  base: '/docs/',
  
  head: [
    ['link', { rel: 'icon', href: '/favicon.ico' }],
    ['meta', { name: 'theme-color', content: '#3c8772' }]
  ],

  themeConfig: {
    logo: '/logo.png',
    
    nav: [
      { text: '首页', link: '/' },
      { text: '快速开始', link: '/guide/getting-started' },
      { text: '用户指南', link: '/guide/user-guide' },
      // { text: '开发指南', link: '/guide/development' }
    ],

    sidebar: {
      '/guide/': [
        {
          text: '开始使用',
          items: [
            { text: '项目介绍', link: '/guide/introduction' },
            { text: '快速开始', link: '/guide/getting-started' },
          ]
        },
        {
          text: '用户指南',
          items: [
            { text: '规则管理', link: '/guide/rule-management' },
            { text: '规则测试', link: '/guide/rule-testing' },
            { text: 'Monaco Editor', link: '/guide/monaco-editor' }
          ]
        },
        // {
        //   text: '开发指南',
        //   items: [
        //     { text: '后端开发', link: '/guide/backend-development' },
        //     { text: '前端开发', link: '/guide/frontend-development' },
        //   ]
        // }
      ],
      '/api/': [
        {
          text: 'API 参考',
          items: [
            { text: '概览', link: '/api/' },
            { text: '规则 API', link: '/api/rules' },
            { text: '测试 API', link: '/api/testing' },
            { text: '验证 API', link: '/api/validation' }
          ]
        }
      ]
    },

    socialLinks: [
      { icon: 'github', link: 'https://github.com/modakai/drools_dynamic' },
      { icon: 'gitee', link: 'https://gitee.com/kk-2049/drools_dynamic' },
    ],

    footer: {
      message: '基于 MIT 许可证发布',
      copyright: 'Copyright © 2025 Drools 动态规则管理系统'
    },

    editLink: {
      pattern: 'https://github.com/your-username/drools-dynamic-rules/edit/main/front/docs/:path',
      text: '在 GitHub 上编辑此页面'
    },

    lastUpdated: {
      text: '最后更新于',
      formatOptions: {
        dateStyle: 'short',
        timeStyle: 'medium'
      }
    },

    docFooter: {
      prev: '上一页',
      next: '下一页'
    },

    outline: {
      label: '页面导航'
    },

    returnToTopLabel: '回到顶部',
    sidebarMenuLabel: '菜单',
    darkModeSwitchLabel: '主题',
    lightModeSwitchTitle: '切换到浅色模式',
    darkModeSwitchTitle: '切换到深色模式'
  },

  markdown: {
    theme: 'material-theme-palenight',
    lineNumbers: true
  }
})