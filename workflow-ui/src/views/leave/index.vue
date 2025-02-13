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

    <!-- 添加离职申请对话框 -->
    <el-dialog 
      title="离职申请" 
      v-model="dialogVisible" 
      width="500px"
    >
      <el-form 
        ref="leaveForm" 
        :model="leaveForm" 
        :rules="rules" 
        label-width="100px"
      >
        <el-form-item label="姓名" prop="name">
          <el-input v-model="leaveForm.name" placeholder="请输入姓名" />
        </el-form-item>
        
        <el-form-item label="离职日期" prop="leaveDate">
          <el-date-picker
            v-model="leaveForm.leaveDate"
            type="date"
            placeholder="选择离职日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>
        
        <el-form-item label="离职原因" prop="reason">
          <el-input
            v-model="leaveForm.reason"
            type="textarea"
            :rows="4"
            placeholder="请输入离职原因"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="info" @click="saveAsDraft">暂存</el-button>
          <el-button type="primary" @click="submitForm">提交</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 添加处理任务对话框 -->
    <el-dialog 
      title="处理申请" 
      v-model="processDialogVisible" 
      width="500px"
    >
      <el-form 
        ref="processForm" 
        :model="processForm" 
        :rules="processRules" 
        label-width="100px"
      >
        <el-form-item label="处理结果" prop="action">
          <el-radio-group v-model="processForm.action">
            <el-radio label="approve">同意</el-radio>
            <el-radio label="reject">拒绝</el-radio>
          </el-radio-group>
        </el-form-item>
        
        <el-form-item label="处理意见" prop="comment">
          <el-input
            v-model="processForm.comment"
            type="textarea"
            :rows="4"
            placeholder="请输入处理意见"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="processDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitProcess">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 添加详情对话框 -->
    <el-dialog 
      title="申请详情" 
      v-model="detailDialogVisible" 
      width="500px"
    >
      <div class="detail-content" v-if="detailInfo">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="申请人">
            {{ detailInfo.name }}
          </el-descriptions-item>
          <el-descriptions-item label="离职日期">
            {{ detailInfo.leaveDate }}
          </el-descriptions-item>
          <el-descriptions-item label="离职原因">
            {{ detailInfo.reason }}
          </el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="getStatusType(detailInfo.status)">
              {{ getStatusText(detailInfo.status) }}
            </el-tag>
          </el-descriptions-item>
        </el-descriptions>
      </div>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="detailDialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { 
  getMyApplications, 
  getTodoTasks, 
  getDoneTasks,
  saveDraft,
  submitLeave,
  getLeaveDetail,
  processLeave,
  deleteLeave
} from '@/api/leave'
import { ElMessage, ElMessageBox } from 'element-plus'

export default {
  name: 'LeaveApplication',
  data() {
    return {
      activeTab: 'apply',
      applyList: [], // 离职申请列表
      todoList: [], // 待办理列表
      doneList: [], // 已办理列表
      dialogVisible: false,
      processDialogVisible: false,
      leaveForm: {
        name: '',
        leaveDate: '',
        reason: '',
        status: 'DRAFT' // DRAFT-草稿, PENDING-待审批
      },
      processForm: {
        id: null,
        action: 'approve',
        comment: ''
      },
      rules: {
        name: [
          { required: true, message: '请输入姓名', trigger: 'blur' }
        ],
        leaveDate: [
          { required: true, message: '请选择离职日期', trigger: 'change' }
        ],
        reason: [
          { required: true, message: '请输入离职原因', trigger: 'blur' }
        ]
      },
      processRules: {
        action: [
          { required: true, message: '请选择处理结果', trigger: 'change' }
        ],
        comment: [
          { required: true, message: '请输入处理意见', trigger: 'blur' }
        ]
      },
      loading: false,
      detailDialogVisible: false,
      detailInfo: null
    }
  },
  created() {
    this.loadData()
  },
  methods: {
    // 加载所有数据
    async loadData() {
      this.loading = true
      try {
        await Promise.all([
          this.loadApplyList(),
          this.loadTodoList(),
          this.loadDoneList()
        ])
      } catch (error) {
        console.error('加载数据失败:', error)
      } finally {
        this.loading = false
      }
    },

    // 加载申请列表
    async loadApplyList() {
      try {
        const res = await getMyApplications()
        this.applyList = res.data || []
      } catch (error) {
        console.error('加载申请列表失败:', error)
        ElMessage.error('加载申请列表失败')
      }
    },

    // 加载待办列表
    async loadTodoList() {
      try {
        const res = await getTodoTasks()
        this.todoList = res.data || []
      } catch (error) {
        console.error('加载待办列表失败:', error)
        ElMessage.error('加载待办列表失败')
      }
    },

    // 加载已办列表
    async loadDoneList() {
      try {
        const res = await getDoneTasks()
        this.doneList = res.data || []
      } catch (error) {
        console.error('加载已办列表失败:', error)
        ElMessage.error('加载已办列表失败')
      }
    },

    // 暂存为草稿
    saveAsDraft() {
      this.$refs.leaveForm.validate(async (valid) => {
        if (valid) {
          try {
            await saveDraft(this.leaveForm)
            ElMessage.success('暂存成功')
            this.dialogVisible = false
            this.loadApplyList()
          } catch (error) {
            console.error('暂存失败:', error)
            ElMessage.error('暂存失败')
          }
        }
      })
    },

    // 提交申请
    submitForm() {
      this.$refs.leaveForm.validate(async (valid) => {
        if (valid) {
          try {
            await submitLeave(this.leaveForm)
            ElMessage.success('提交成功')
            this.dialogVisible = false
            this.loadApplyList()
          } catch (error) {
            console.error('提交失败:', error)
            ElMessage.error('提交失败')
          }
        }
      })
    },

    // 查看详情
    async viewDetail(id) {
      try {
        const res = await getLeaveDetail(id)
        if (res.code === 0) {  // 假设成功状态码为0
          this.detailInfo = res.data
          this.detailDialogVisible = true
        } else {
          ElMessage.error(res.msg || '获取详情失败')
        }
      } catch (error) {
        console.error('获取详情失败:', error)
        ElMessage.error('获取详情失败')
      }
    },

    // 处理任务
    async handleTask(id) {
      this.processForm = {
        id,
        action: 'approve',
        comment: ''
      }
      this.processDialogVisible = true
    },

    // 提交处理结果
    async submitProcess() {
      this.$refs.processForm.validate(async (valid) => {
        if (valid) {
          try {
            await processLeave(this.processForm)
            ElMessage.success('处理成功')
            this.processDialogVisible = false
            this.loadData()
          } catch (error) {
            console.error('处理失败:', error)
            ElMessage.error('处理失败')
          }
        }
      })
    },

    // 删除申请
    async deleteApplication(id) {
      try {
        await ElMessageBox.confirm('确定要删除该申请吗？', '提示', {
          type: 'warning'
        })
        await deleteLeave(id)
        ElMessage.success('删除成功')
        this.loadApplyList()
      } catch (error) {
        if (error !== 'cancel') {
          console.error('删除失败:', error)
          ElMessage.error('删除失败')
        }
      }
    },

    createApplication() {
      this.leaveForm = {
        name: '',
        leaveDate: '',
        reason: '',
        status: 'DRAFT'
      }
      this.dialogVisible = true
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

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.detail-content {
  padding: 20px;
}

:deep(.el-descriptions__label) {
  width: 120px;
  justify-content: flex-end;
}

:deep(.el-descriptions__content) {
  padding: 12px;
}
</style> 