<template>
  <div class="leave-container">
    <el-tabs v-model="activeTab" class="leave-tabs">
      <el-tab-pane label="离职申请" name="apply">
        <div class="tab-content">
          <div class="header">
            <h3>离职申请</h3>
            <el-button type="primary" @click="createApplication">新建申请</el-button>
          </div>
          <el-table :data="applyList" style="width: 100%; margin-top: 20px">
            <el-table-column prop="id" label="申请编号" width="180" />
            <el-table-column prop="reason" label="离职原因" />
            <el-table-column prop="status" label="状态">
              <template #default="{ row }">
                <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="申请时间">
              <template #default="{ row }">
                {{ formatDate(row.createTime) }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="150">
              <template #default="{ row }">
                <el-button type="primary" size="small" @click="viewDetail(row.id)">查看</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-tab-pane>

      <el-tab-pane label="待办理" name="todo">
        <div class="tab-content">
          <div class="header">
            <h3>待办理</h3>
          </div>
          <el-table :data="todoList" style="width: 100%; margin-top: 20px">
            <el-table-column prop="id" label="申请编号" width="180" />
            <el-table-column prop="applicant" label="申请人" />
            <el-table-column prop="reason" label="离职原因" />
            <el-table-column prop="nodeName" label="当前节点" />
            <el-table-column prop="createTime" label="申请时间">
              <template #default="{ row }">
                {{ formatDate(row.createTime) }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="150">
              <template #default="{ row }">
                <el-button type="primary" size="small" @click="handleTask(row.id)">处理</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-tab-pane>

      <el-tab-pane label="已办理" name="done">
        <div class="tab-content">
          <div class="header">
            <h3>已办理</h3>
          </div>
          <el-table :data="doneList" style="width: 100%; margin-top: 20px">
            <el-table-column prop="id" label="申请编号" width="180" />
            <el-table-column prop="applicant" label="申请人" />
            <el-table-column prop="reason" label="离职原因" />
            <el-table-column prop="status" label="状态">
              <template #default="{ row }">
                <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="handleTime" label="处理时间">
              <template #default="{ row }">
                {{ formatDate(row.handleTime) }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="150">
              <template #default="{ row }">
                <el-button type="primary" size="small" @click="viewDetail(row.id)">查看</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
export default {
  name: 'LeaveApplication',
  data() {
    return {
      activeTab: 'apply',
      applyList: [], // 离职申请列表
      todoList: [], // 待办理列表
      doneList: [], // 已办理列表
    }
  },
  methods: {
    createApplication() {
      // TODO: 实现新建申请逻辑
      console.log('创建新申请')
    },
    viewDetail(id) {
      // TODO: 实现查看详情逻辑
      console.log('查看详情:', id)
    },
    handleTask(id) {
      // TODO: 实现任务处理逻辑
      console.log('处理任务:', id)
    },
    getStatusType(status) {
      const typeMap = {
        'PENDING': 'warning',
        'APPROVED': 'success',
        'REJECTED': 'danger',
        'PROCESSING': 'primary'
      }
      return typeMap[status] || 'info'
    },
    getStatusText(status) {
      const textMap = {
        'PENDING': '待处理',
        'APPROVED': '已通过',
        'REJECTED': '已拒绝',
        'PROCESSING': '处理中'
      }
      return textMap[status] || status
    },
    formatDate(dateStr) {
      if (!dateStr) return ''
      const date = new Date(dateStr)
      return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
    }
  }
}
</script>

<style scoped>
.leave-container {
  padding: 20px;
}

.leave-tabs {
  background: #fff;
  padding: 20px;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.tab-content {
  padding: 20px 0;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 500;
}

:deep(.el-tabs__nav-wrap::after) {
  height: 1px;
}

:deep(.el-table) {
  margin-top: 20px;
}
</style> 