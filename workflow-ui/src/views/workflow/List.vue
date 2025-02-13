<template>
  <div class="workflow-list">
    <div class="header">
      <h2>工作流列表</h2>
      <button class="add-btn" @click="createWorkflow">新建工作流</button>
    </div>
    
    <div class="table-container">
      <table>
        <thead>
          <tr>
            <th>ID</th>
            <th>名称</th>
            <th>创建时间</th>
            <th>更新时间</th>
            <th>最新版本</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in workflowList" :key="item.id">
            <td>{{ item.id }}</td>
            <td>{{ item.name }}</td>
            <td>{{ formatDate(item.createTime) }}</td>
            <td>{{ formatDate(item.updateTime) }}</td>
            <td>v{{ item.currentVersion || 0 }}</td>
            <td>
              <button class="edit-btn" @click="editWorkflow(item.id)">配置</button>
              <button class="publish-btn" @click="publishWorkflow(item.id)">发布</button>
              <button class="view-btn" @click="viewInstance(item.id)">查看实例</button>
              <el-dropdown @command="handleVersionCommand" trigger="click">
                <button class="version-btn">
                  版本
                  <el-icon><ArrowDown /></el-icon>
                </button>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item 
                      v-for="version in item.versions" 
                      :key="version.id"
                      :command="{ action: 'viewVersion', workflowId: item.id, version: version.version }"
                    >
                      v{{ version.version }} ({{ formatDate(version.createTime) }})
                    </el-dropdown-item>
                    <el-dropdown-item v-if="!item.versions || item.versions.length === 0" disabled>
                      暂无版本
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script>
import { workflowApi } from '@/api/workflow'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowDown } from '@element-plus/icons-vue'

export default {
  name: 'WorkflowList',
  components: {
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
        const data = await workflowApi.getWorkflowList()
        this.workflowList = data.map(item => ({
          ...item,
          versions: item.versions || [],
          currentVersion: item.currentVersion || 0
        }))
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
      const routeUrl = this.$router.resolve({
        path: '/workflow/editor'
      })
      window.open(routeUrl.href, '_blank')
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
    async handleVersionCommand({ action, workflowId, version }) {
      if (action === 'viewVersion') {
        const routeUrl = this.$router.resolve({
          path: `/workflow/viewer/${workflowId}`,
          query: { version }
        })
        window.open(routeUrl.href, '_blank')
      }
    },
    viewInstance(workflowId) {
      const routeUrl = this.$router.resolve({
        path: `/workflow/instance/${workflowId}`
      })
      window.open(routeUrl.href, '_blank')
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

.add-btn {
  padding: 8px 16px;
  background-color: #1890ff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.add-btn:hover {
  background-color: #40a9ff;
}

.table-container {
  background: white;
  border-radius: 4px;
  padding: 20px;
}

table {
  width: 100%;
  border-collapse: collapse;
}

th, td {
  padding: 12px 8px;
  text-align: left;
  border-bottom: 1px solid #e8e8e8;
}

th {
  background-color: #fafafa;
  font-weight: 500;
}

.edit-btn {
  padding: 4px 12px;
  background-color: #52c41a;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.edit-btn:hover {
  background-color: #73d13d;
}

.publish-btn {
  padding: 4px 12px;
  background-color: #67c23a;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  margin-left: 8px;
}

.publish-btn:hover {
  background-color: #85ce61;
}

.view-btn {
  padding: 4px 12px;
  background-color: #409eff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  margin-left: 8px;
}

.view-btn:hover {
  background-color: #66b1ff;
}

.version-btn {
  padding: 4px 12px;
  background-color: #909399;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  margin-left: 8px;
  display: inline-flex;
  align-items: center;
  gap: 4px;
}

.version-btn:hover {
  background-color: #a6a9ad;
}

:deep(.el-dropdown-menu__item) {
  line-height: 30px;
  padding: 0 16px;
}

:deep(.el-dropdown-menu__item.is-disabled) {
  color: #909399;
}
</style> 