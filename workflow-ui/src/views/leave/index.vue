<template>
  <div class="leave-container">
    <el-tabs v-model="activeTab" class="leave-tabs" v-loading="loading">
      <el-tab-pane label="请假申请" name="apply" :key="'apply-' + activeTab">
        <div class="tab-content">
          <div class="header">
            <h3>请假申请</h3>
            <el-button type="primary" @click="createApplication">新建申请</el-button>
          </div>
          <el-table :data="applyList" style="width: 100%; margin-top: 20px">
            <el-table-column label="申请编号" prop="id" width="180"/>
            <el-table-column label="申请人" prop="name"/>
            <el-table-column label="离职原因" prop="reason"/>
            <el-table-column label="开始时间" prop="startDate">
              <template #default="{ row }">
                {{ formatDateYMD(row.startDate) }}
              </template>
            </el-table-column>
            <el-table-column label="结束时间" prop="endDate">
              <template #default="{ row }">
                {{ formatDateYMD(row.endDate) }}
              </template>
            </el-table-column>
            <el-table-column label="状态" prop="status">
              <template #default="{ row }">
                <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="申请时间" prop="createTime">
              <template #default="{ row }">
                {{ formatDate(row.createTime) }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="150">
              <template #default="{ row }">
                <el-button size="small" type="primary" @click="viewDetail(row.id)">查看</el-button>
                <el-button size="small" style="margin-left: 5px" type="success" @click="viewProgress(row)">进度查询</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-tab-pane>

      <el-tab-pane label="待办理" name="todo" :key="'todo-' + activeTab">
        <div class="tab-content">
          <div class="header">
            <h3>待办理</h3>
          </div>
          <el-table :data="todoList" style="width: 100%; margin-top: 20px">
            <el-table-column label="申请编号" prop="id" width="180"/>
            <el-table-column label="申请人" prop="name"/>
            <el-table-column label="离职原因" prop="reason"/>
            <el-table-column label="开始时间" prop="startDate">
              <template #default="{ row }">
                {{ formatDateYMD(row.startDate) }}
              </template>
            </el-table-column>
            <el-table-column label="结束时间" prop="endDate">
              <template #default="{ row }">
                {{ formatDateYMD(row.endDate) }}
              </template>
            </el-table-column>
            <el-table-column label="当前节点" prop="nodeName"/>
            <el-table-column label="申请时间" prop="createTime">
              <template #default="{ row }">
                {{ formatDate(row.createTime) }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="150">
              <template #default="{ row }">
                <el-button size="small" type="primary" @click="handleTask(row.id)">处理</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-tab-pane>

      <el-tab-pane label="已办理" name="done" :key="'done-' + activeTab">
        <div class="tab-content">
          <div class="header">
            <h3>已办理</h3>
          </div>
          <el-table :data="doneList" style="width: 100%; margin-top: 20px">
            <el-table-column label="申请编号" prop="id" width="180"/>
            <el-table-column label="申请人" prop="name"/>
            <el-table-column label="开始时间" prop="startDate">
              <template #default="{ row }">
                {{ formatDateYMD(row.startDate) }}
              </template>
            </el-table-column>
            <el-table-column label="结束时间" prop="endDate">
              <template #default="{ row }">
                {{ formatDateYMD(row.endDate) }}
              </template>
            </el-table-column>
            <el-table-column label="离职原因" prop="reason"/>
            <el-table-column label="状态" prop="status">
              <template #default="{ row }">
                <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="处理时间" prop="handleTime">
              <template #default="{ row }">
                {{ formatDate(row.handleTime) }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="150">
              <template #default="{ row }">
                <el-button size="small" type="primary" @click="viewDetail(row.id)">查看</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-tab-pane>
    </el-tabs>

    <!-- 添加请假申请对话框 -->
    <el-dialog v-model="dialogVisible" title="请假申请" width="500px">
      <el-form ref="leaveForm" :model="leaveForm" :rules="rules" label-width="100px">
        <el-form-item label="姓名" prop="name">
          <el-input v-model="leaveForm.name" placeholder="请输入姓名"/>
        </el-form-item>

        <el-form-item label="开始日期" prop="startDate">
          <el-date-picker v-model="leaveForm.startDate" format="YYYY-MM-DD" placeholder="选择离职日期" style="width: 100%" type="date" value-format="YYYY-MM-DD"/>
        </el-form-item>

        <el-form-item label="结束日期" prop="endDate">
          <el-date-picker v-model="leaveForm.endDate" format="YYYY-MM-DD" placeholder="选择离职日期" style="width: 100%" type="date" value-format="YYYY-MM-DD"/>
        </el-form-item>

        <el-form-item label="离职原因" prop="reason">
          <el-input v-model="leaveForm.reason" :rows="4" placeholder="请输入离职原因" type="textarea"/>
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

    <!-- 封装后的处理任务对话框组件，只传业务ID -->
    <ProcessDialog v-model="processDialogVisible" :businessId="selectedBusinessId" @submit="submitProcess"/>

    <!-- 添加详情对话框 -->
    <el-dialog v-model="detailDialogVisible" title="申请详情" width="500px">
      <div v-if="detailInfo" class="detail-content">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="申请人">
            {{ detailInfo.name }}
          </el-descriptions-item>
          <el-descriptions-item label="开始日期">
            {{ formatDateYMD(detailInfo.startDate) }}
          </el-descriptions-item>
          <el-descriptions-item label="结束日期">
            {{ formatDateYMD(detailInfo.endDate) }}
          </el-descriptions-item>
          <el-descriptions-item label="原因">
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
import {ElMessage, ElMessageBox} from 'element-plus'
import {leaveApi} from "@/api/leave";
import ProcessDialog from "@/components/task/ProcessDialog.vue";

export default {
  name: 'LeaveApplication',
  components: {ProcessDialog},
  data() {
    return {
      activeTab: 'apply',
      applyList: [], // 请假申请列表
      todoList: [], // 待办理列表
      doneList: [], // 已办理列表
      dialogVisible: false,
      processDialogVisible: false,
      // 用于传递待处理任务业务ID，ProcessDialog 会根据 businessId 加载对应任务数据
      selectedBusinessId: null,
      leaveForm: {
        name: '',
        startDate: '',
        endDate: '',
        reason: '',
        status: 'DRAFT'
      },
      rules: {
        name: [
          {required: true, message: '请输入姓名', trigger: 'blur'}
        ],
        startDate: [
          {required: true, message: '请选择开始日期', trigger: 'change'}
        ],
        endDate: [
          {required: true, message: '请选择结束日期', trigger: 'change'}
        ],
        reason: [
          {required: true, message: '请输入原因', trigger: 'blur'}
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
  watch: {
    activeTab: {
      handler(newVal) {
        switch (newVal) {
          case 'apply':
            this.loadApplyList();
            break;
          case 'todo':
            this.loadTodoList();
            break;
          case 'done':
            this.loadDoneList();
            break;
        }
      }
    }
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
        ElMessage.error('加载数据失败')
      } finally {
        this.loading = false
      }
    },

    // 加载申请列表
    async loadApplyList() {
      this.loading = true
      try {
        const res = await leaveApi.getMyApplications()
        this.applyList = res || []
      } catch (error) {
        console.error('加载申请列表失败:', error)
        ElMessage.error('加载申请列表失败')
      } finally {
        this.loading = false
      }
    },

    // 加载待办列表
    async loadTodoList() {
      this.loading = true
      try {
        const res = await leaveApi.getTodoTasks()
        this.todoList = res || []
      } catch (error) {
        console.error('加载待办列表失败:', error)
        ElMessage.error('加载待办列表失败')
      } finally {
        this.loading = false
      }
    },

    // 加载已办列表
    async loadDoneList() {
      this.loading = true
      try {
        const res = await leaveApi.getDoneTasks()
        this.doneList = res || []
      } catch (error) {
        console.error('加载已办列表失败:', error)
        ElMessage.error('加载已办列表失败')
      } finally {
        this.loading = false
      }
    },

    // 暂存为草稿
    saveAsDraft() {
      this.$refs.leaveForm.validate(async (valid) => {
        if (valid) {
          try {
            await leaveApi.saveDraft(this.leaveForm)
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
            await leaveApi.submitLeave(this.leaveForm)
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
        const res = await leaveApi.getLeaveDetail(id)
        if (res.code === 0) {  // 假设成功状态码为0
          this.detailInfo = res
          this.detailDialogVisible = true
        } else {
          ElMessage.error(res.msg || '获取详情失败')
        }
      } catch (error) {
        console.error('获取详情失败:', error)
        ElMessage.error('获取详情失败')
      }
    },

    // 修改处理任务方法，只传入业务ID，弹框内组件会根据业务ID自行加载任务数据及操作配置
    async handleTask(id) {
      this.selectedBusinessId = id;
      this.processDialogVisible = true;
    },

    // ProcessDialog 提交后回调，刷新数据
    async submitProcess() {
      this.loadData();
    },

    // 删除申请
    async deleteApplication(id) {
      try {
        await ElMessageBox.confirm('确定要删除该申请吗？', '提示', {
          type: 'warning'
        })
        await leaveApi.deleteLeave(id)
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
        startDate: '',
        endDate: '',
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
    },

    // 添加新的日期格式化方法（仅显示年月日）
    formatDateYMD(dateStr) {
      if (!dateStr) return '';
      const date = new Date(dateStr);
      return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`;
    },

    // 进度查询：跳转到流程图页面（实例详情页面）
    viewProgress(row) {
      // 判断是否存在工作流实例ID（workflowVersionId字段保存了流程启动后返回的ID）
      if (!row.workflowVersionId) {
        ElMessage.info('当前申请尚未提交或流程实例未生成')
        return
      }
      // 使用路由跳转到实例详情页面，实例详情页面由 InstanceDetail.vue 实现
      // 构造目标路由地址
      const routeData = this.$router.resolve({
        name: 'InstanceDetail',
        params: {instanceId: row.id}
      });

      // 使用 window.open 打开新标签页
      window.open(routeData.href, '_blank');
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
  min-height: 300px;
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
  height: 100%;
  min-height: 200px;
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