<template>
  <div class="instance-detail">
    <h2>流程实例详情</h2>
    <el-card v-if="instance" class="instance-card">
      <p><strong>实例ID:</strong> {{ instance.id }}</p>
      <p><strong>业务关联:</strong> {{ instance.businessKey }}</p>
      <p><strong>当前节点:</strong> {{ instance.currentNode }}</p>
      <p><strong>状态:</strong> {{ instance.status }}</p>
      <p><strong>版本号:</strong> {{ instance.version }}</p>
      <p><strong>创建时间:</strong> {{ formatDate(instance.createTime) }}</p>
      <p><strong>更新时间:</strong> {{ formatDate(instance.updateTime) }}</p>
    </el-card>

    <el-card v-if="approvalRecords.length" class="approval-card" style="margin-top:20px;">
      <h3>审批记录</h3>
      <el-table :data="approvalRecords" style="width: 100%">
        <el-table-column prop="id" label="记录ID"></el-table-column>
        <el-table-column prop="approver" label="审批人"></el-table-column>
        <el-table-column prop="result" label="审批结果"></el-table-column>
        <el-table-column prop="comment" label="审批意见"></el-table-column>
        <el-table-column label="审批时间">
          <template #default="scope">
            {{ formatDate(scope.row.createTime) }}
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script>
import { instanceApi } from '@/api/workflow/instance'
import { ElMessage } from 'element-plus'

export default {
  name: 'InstanceDetail',
  props: {
    instanceId: {
      type: [String, Number],
      required: true
    }
  },
  data() {
    return {
      instance: null,
      approvalRecords: []
    }
  },
  created() {
    this.loadInstance()
    this.loadApprovalRecords()
  },
  methods: {
    async loadInstance() {
      try {
        const res = await instanceApi.getWorkflowInstance(this.instanceId)
        this.instance = res
      } catch (error) {
        console.error('加载实例详情失败:', error)
        ElMessage.error('加载实例详情失败')
      }
    },
    async loadApprovalRecords() {
      try {
        const res = await instanceApi.getApprovalRecords(this.instanceId)
        this.approvalRecords = Array.isArray(res) ? res : (res.data || [])
      } catch (error) {
        console.error('加载审批记录失败:', error)
        ElMessage.error('加载审批记录失败')
      }
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
.instance-detail {
  padding: 20px;
}
.instance-card {
  padding: 20px;
}
.approval-card {
  padding: 20px;
}
</style> 