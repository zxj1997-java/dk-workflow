<template>
  <div class="instance-list">
    <div class="header">
      <h2>{{ workflow?.name }} - 流程实例列表</h2>
    </div>

    <div class="table-container">
      <el-table :data="instanceList" style="width: 100%">
        <el-table-column prop="id" label="实例ID"/>
        <el-table-column prop="businessKey" label="业务关联"/>
        <el-table-column prop="currentNode" label="当前节点"/>
        <el-table-column label="状态">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间">
          <template #default="scope">
            {{ formatDate(scope.row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="updateTime" label="更新时间">
          <template #default="scope">
            {{ formatDate(scope.row.updateTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="scope">
            <el-button type="primary" size="small" @click="viewInstance(scope.row.id,scope.row.version)">
              查看
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script>
import {instanceApi} from '@/api/workflow/instance'
import {workflowApi} from '@/api/workflow'
import {ElMessage} from 'element-plus'

export default {
  name: 'InstanceList',
  props: {
    workflowId: {
      type: [String, Number],
      required: true
    }
  },
  data() {
    return {
      workflow: null,
      instanceList: []
    }
  },
  created() {
    this.loadWorkflow()
    this.loadInstances()
  },
  methods: {
    async loadWorkflow() {
      try {
        const res = await workflowApi.getWorkflowById(this.workflowId)
        this.workflow = res
      } catch (error) {
        console.error('加载工作流失败:', error)
        ElMessage.error('加载工作流失败')
      }
    },
    async loadInstances() {
      try {
        const res = await instanceApi.getWorkflowInstances(this.workflowId)
        console.log(res)
        this.instanceList = Array.isArray(res) ? res : (res.data || [])
      } catch (error) {
        console.error('获取实例列表失败:', error)
        ElMessage.error('获取实例列表失败')
      }
    },
    formatDate(dateStr) {
      if (!dateStr) return ''
      const date = new Date(dateStr)
      return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
    },
    getStatusType(status) {
      const statusMap = {
        RUNNING: 'primary',
        COMPLETED: 'success',
        TERMINATED: 'danger',
        SUSPENDED: 'warning'
      }
      return statusMap[status] || 'info'
    },
    getStatusText(status) {
      const statusMap = {
        RUNNING: '进行中',
        COMPLETED: '已完成',
        TERMINATED: '已终止',
        SUSPENDED: '已暂停'
      }
      return statusMap[status] || status
    },
    viewInstance(instanceId) {
      const routeUrl = this.$router.resolve({
        name: 'InstanceDetail',
        params: {instanceId}
      })
      window.open(routeUrl.href, '_blank')
    }
  }
}
</script>

<style scoped>
.instance-list {
  padding: 20px;
}

.header {
  margin-bottom: 20px;
}

.header h2 {
  margin: 0;
  font-size: 20px;
  font-weight: 500;
}

.table-container {
  background: #fff;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}
</style> 