<template>
  <div class="workflow-list">
    <div class="header">
      <h2>工作流列表</h2>
      <el-button type="primary" @click="createWorkflow">新建工作流</el-button>
    </div>

    <el-table :data="workflowList" style="width: 100%; margin-top: 20px">
      <el-table-column label="工作流名称" prop="name"/>
      <el-table-column label="工作流编码" prop="code"/>
      <el-table-column label="状态" prop="status"/>
      <el-table-column label="创建时间" prop="createTime">
        <template #default="scope">
          {{ formatDate(scope.row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column label="最新版本">
        <template #default="scope">
          v{{ scope.row.currentVersion || 0 }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="400">
        <template #default="scope">
          <el-button size="small" type="primary" @click="editWorkflow(scope.row.id)">配置</el-button>
          <el-button size="small" type="success" @click="publishWorkflow(scope.row.id)">发布</el-button>
          <el-dropdown trigger="click" @command="handleVersionCommand">
            <el-button size="small" type="warning">
              版本
              <el-icon class="el-icon--right">
                <arrow-down/>
              </el-icon>
            </el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item v-for="version in (scope.row.versions || [])" :key="version.id" :command="{ action: 'viewVersion', workflowId: scope.row.id, version: version.version }">
                  v{{ version.version }} ({{ formatDate(version.createTime) }})
                </el-dropdown-item>
                <el-dropdown-item v-if="!scope.row.versions?.length" disabled>
                  暂无版本
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>
      </el-table-column>
    </el-table>

    <CreateDialog ref="createDialog" @created="handleWorkflowCreated"/>
  </div>
</template>

<script>
import CreateDialog from '@/components/workflow/CreateDialog.vue'
import {workflowApi} from '@/api/workflow'
import {ElMessage, ElMessageBox} from 'element-plus'
import {ArrowDown} from '@element-plus/icons-vue'

export default {
  name: 'WorkflowList',
  components: {
    CreateDialog,
    ArrowDown
  },
  data() {
    return {
      workflowList: []
    }
  },
  created() {
    this.getList()
  },
  methods: {
    async getList() {
      try {
        const res = await workflowApi.getWorkflowList()

        if (Array.isArray(res)) {
          this.workflowList = res.map(item => ({
            ...item,
            versions: item.versions || [],
            currentVersion: item.currentVersion || 0
          }))
        } else if (res && res) {
          this.workflowList = (Array.isArray(res) ? res : []).map(item => ({
            ...item,
            versions: item.versions || [],
            currentVersion: item.currentVersion || 0
          }))
        } else {
          console.error('API返回数据格式不符合预期:', res)
          this.workflowList = []
        }

      } catch (error) {
        console.error('获取列表失败:', error)
        ElMessage.error('获取列表失败')
      }
    },
    formatDate(dateStr) {
      if (!dateStr) return ''
      const date = new Date(dateStr)
      return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
    },
    createWorkflow() {
      this.$refs.createDialog.show()
    },
    handleWorkflowCreated() {
      this.getList()
    },
    editWorkflow(id) {
      const routeUrl = this.$router.resolve({
        path: `/workflow/editor/${id}`
      })
      window.open(routeUrl.href, '_blank')
    },
    async publishWorkflow(id) {
      try {
        await ElMessageBox.confirm(
            '发布后将生成新的版本，确定要发布吗？',
            '发布确认',
            {
              confirmButtonText: '确定',
              cancelButtonText: '取消',
              type: 'warning'
            }
        )

        await workflowApi.publishWorkflow(id)
        ElMessage.success('发布成功')
        this.getList()
      } catch (error) {
        if (error !== 'cancel') {
          console.error('发布失败:', error)
          ElMessage.error('发布失败')
        }
      }
    },
    async handleVersionCommand({action, workflowId, version}) {
      if (action === 'viewVersion') {
        const routeUrl = this.$router.resolve({
          path: `/workflow/viewer/${workflowId}`,
          query: {version}
        })
        window.open(routeUrl.href, '_blank')
      }
    }
  }
}
</script>

<style scoped>
.workflow-list {
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.header h2 {
  margin: 0;
  font-size: 20px;
  font-weight: 500;
}

:deep(.el-dropdown) {
  margin-left: 8px;
}

:deep(.el-button) {
  margin-right: 8px;
}

:deep(.el-button):last-child {
  margin-right: 0;
}

:deep(.el-dropdown-menu__item) {
  line-height: 30px;
  padding: 0 16px;
}

:deep(.el-dropdown-menu__item.is-disabled) {
  color: #909399;
}
</style> 