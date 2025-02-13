/* eslint-disable vue/multi-word-component-names */
<template>
  <el-container class="layout-container">
    <el-aside width="200px" class="aside">
      <el-menu
        :default-active="activeMenu"
        router
        class="menu"
      >
        <el-menu-item index="/leave/index">
          <el-icon><Document /></el-icon>
          <span>离职申请</span>
        </el-menu-item>
        
        <el-sub-menu index="/workflow">
          <template #title>
            <el-icon><Setting /></el-icon>
            <span>工作流管理</span>
          </template>
          <el-menu-item index="/workflow/list">流程列表</el-menu-item>
        </el-sub-menu>
      </el-menu>
    </el-aside>
    
    <el-container>
      <el-header class="header">
        <div class="breadcrumb">
          <el-breadcrumb>
            <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item>{{ currentRoute }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
      </el-header>
      
      <el-main class="main">
        <router-view></router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script>
import { Document, Setting } from '@element-plus/icons-vue'

export default {
  name: 'AppLayout',
  components: {
    Document,
    Setting
  },
  computed: {
    activeMenu() {
      return this.$route.path
    },
    currentRoute() {
      return this.$route.meta.title || ''
    }
  }
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
}

.aside {
  background-color: #304156;
  color: #fff;
}

.menu {
  height: 100%;
  border-right: none;
}

.header {
  background-color: #fff;
  border-bottom: 1px solid #e6e6e6;
  display: flex;
  align-items: center;
  padding: 0 20px;
}

.main {
  background-color: #f0f2f5;
  padding: 20px;
}

.breadcrumb {
  line-height: 60px;
}
</style> 