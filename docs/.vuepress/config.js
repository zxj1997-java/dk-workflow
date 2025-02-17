import { defineUserConfig } from 'vuepress'
import { defaultTheme } from '@vuepress/theme-default'
import { viteBundler } from '@vuepress/bundler-vite'

export default defineUserConfig({
  lang: 'zh-CN',
  title: '登科流程引擎',
  description: '一个基于 Spring Boot 的轻量级工作流引擎',
  bundler: viteBundler(),
  
  theme: defaultTheme({
    logo: '/images/logo.png',
    repo: 'https://gitee.com/zhang_xing_ju/dk-workflow',
    docsDir: 'docs',
    
    navbar: [
      { text: '指南', link: '/guide/' },
      { text: '配置', link: '/config/' },
      { text: 'API', link: '/api/' },
      { text: '示例', link: '/examples/' },
      { 
        text: '了解更多', 
        children: [
          { text: 'GitHub', link: 'https://github.com/zhang_xing_ju/dk-workflow' },
          { text: 'Gitee', link: 'https://gitee.com/zhang_xing_ju/dk-workflow' }
        ]
      }
    ],
    
    sidebar: {
      '/guide/': [
        {
          text: '介绍',
          children: [
            '/guide/README.md',
            '/guide/getting-started.md',
          ]
        },
        {
          text: '核心概念',
          children: [
            '/guide/workflow.md',
            '/guide/activity.md',
            '/guide/transition.md',
          ]
        },
        {
          text: '进阶使用',
          children: [
            '/guide/custom-handler.md',
            '/guide/version-control.md',
          ]
        }
      ],
      '/config/': [
        {
          text: '配置',
          children: [
            '/config/README.md',
            '/config/database.md',
            '/config/user-system.md',
          ]
        }
      ],
      '/api/': [
        {
          text: 'API 参考',
          children: [
            '/api/README.md',
            '/api/workflow.md',
            '/api/task.md',
          ]
        }
      ],
      '/examples/': [
        {
          text: '示例',
          children: [
            '/examples/README.md',
            '/examples/screenshots.md',
            '/examples/leave-process.md',
            '/examples/approval-process.md',
          ]
        }
      ]
    }
  })
}) 