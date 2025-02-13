<template>
  <div class="workflow-viewer">
    <div class="header">
      <h2>{{ workflow?.name }} - v{{ version }}</h2>
    </div>
    <div id="container" ref="container"></div>

    <!-- 添加属性抽屉 -->
    <el-drawer
      v-model="drawerVisible"
      :title="drawerTitle"
      size="400px"
      :destroy-on-close="true"
    >
      <div class="drawer-container" v-if="selectedData">
        <!-- 活动属性 -->
        <template v-if="selectedType === 'activity'">
          <div class="property-item">
            <label>活动名称：</label>
            <span>{{ selectedData.name }}</span>
          </div>
          <div class="property-item">
            <label>活动编码：</label>
            <span>{{ selectedData.code }}</span>
          </div>
          <div class="property-item">
            <label>跳转页面：</label>
            <span>{{ selectedData.pageUrl }}</span>
          </div>
          <div class="property-item">
            <label>审核人员：</label>
            <div class="value-list">
              <el-tag v-for="user in selectedData.approvers" :key="user" size="small">
                {{ getUserName(user) }}
              </el-tag>
            </div>
          </div>
          <div class="property-item">
            <label>审核部门：</label>
            <div class="value-list">
              <el-tag v-for="dept in selectedData.departments" :key="dept" size="small">
                {{ getDeptName(dept) }}
              </el-tag>
            </div>
          </div>
          <div class="property-item">
            <label>操作按钮：</label>
            <div class="value-list">
              <el-tag v-for="op in selectedData.operations" :key="op" size="small">
                {{ getOperationName(op) }}
              </el-tag>
            </div>
          </div>
        </template>

        <!-- 变迁属性 -->
        <template v-else-if="selectedType === 'transition'">
          <div class="property-item">
            <label>变迁名称：</label>
            <span>{{ selectedData.name }}</span>
          </div>
          <div class="property-item">
            <label>变迁编码：</label>
            <span>{{ selectedData.code }}</span>
          </div>
          <div class="property-item">
            <label>条件类：</label>
            <span>{{ selectedData.conditionClass }}</span>
          </div>
        </template>
      </div>
    </el-drawer>
  </div>
</template>

<script>
import { Graph } from '@antv/x6'
import { workflowApi,versionApi } from '@/api/workflow'
import { ElMessage } from 'element-plus'

// 注册节点类型
Graph.registerNode(
  'event',
  {
    inherit: 'circle',
    attrs: {
      body: {
        strokeWidth: 2,
        stroke: '#5F95FF',
        fill: '#FFF',
      },
    },
  },
  true,
)

Graph.registerNode(
  'activity',
  {
    inherit: 'rect',
    markup: [
      {
        tagName: 'rect',
        selector: 'body',
      },
      {
        tagName: 'text',
        selector: 'label',
      },
    ],
    attrs: {
      body: {
        rx: 6,
        ry: 6,
        stroke: '#5F95FF',
        fill: '#EFF4FF',
        strokeWidth: 1,
      },
      label: {
        fontSize: 12,
        fill: '#262626',
      },
    },
  },
  true,
)

export default {
  name: 'WorkflowViewer',
  props: {
    id: {
      type: String,
      required: true
    },
    version: {
      type: String,
      required: true
    }
  },
  data() {
    return {
      graph: null,
      workflow: null,
      workflowData: null,
      drawerVisible: false,
      drawerTitle: '',
      selectedType: '',
      selectedData: null,
      userOptions: [],
      departmentOptions: []
    }
  },
  async created() {
    await Promise.all([
      this.loadWorkflowData(),
      this.loadOptions()
    ])
  },
  mounted() {
    this.initGraph()
  },
  watch: {
    workflowData: {
      handler(newData) {
        if (newData && this.graph) {
          this.$nextTick(() => {
            this.renderWorkflow()
          })
        }
      },
      immediate: true
    }
  },
  methods: {
    async loadWorkflowData() {
      try {
        // 获取工作流基本信息
        this.workflow = await workflowApi.getWorkflowById(this.id)
        
        // 获取指定版本的数据
        const versionData = await versionApi.getWorkflowVersion(this.id, this.version)
        if (versionData && versionData.flowData) {
          this.workflowData = JSON.parse(versionData.flowData)
          console.log('加载的工作流数据:', this.workflowData)
        } else {
          throw new Error('版本数据为空')
        }
      } catch (error) {
        console.error('加载工作流数据失败:', error)
        ElMessage.error('加载工作流数据失败')
      }
    },
    async loadOptions() {
      try {
        const [users, departments] = await Promise.all([
          workflowApi.getUsers(),
          workflowApi.getDepartments()
        ])
        this.userOptions = users
        this.departmentOptions = departments
      } catch (error) {
        console.error('加载选项数据失败:', error)
      }
    },
    initGraph() {
      const container = this.$refs.container
      if (!container) return

      const { width, height } = container.getBoundingClientRect()
      
      this.graph = new Graph({
        container: container,
        width,
        height,
        grid: {
          size: 10,
          visible: true,
          type: 'dot',
          args: {
            color: '#cccccc',
            thickness: 1,
          },
        },
        interacting: false, // 禁止交互
        connecting: {
          router: {
            name: 'normal'
          },
          connector: {
            name: 'normal'
          }
        }
      })

      // 添加窗口大小变化监听
      window.addEventListener('resize', this.updateGraphSize)

      // 添加节点点击事件
      this.graph.on('node:click', ({ node }) => {
        const data = node.getData()
        if (data && data.type === 'activity') {
          this.showProperties('activity', data)
        }
      })

      // 添加边点击事件
      this.graph.on('edge:click', ({ edge }) => {
        const data = edge.getData()
        if (data) {
          this.showProperties('transition', data)
        }
      })
    },
    updateGraphSize() {
      const container = this.$refs.container
      if (container && this.graph) {
        const { width, height } = container.getBoundingClientRect()
        this.graph.resize(width, height)
      }
    },
    renderWorkflow() {
      if (!this.workflowData || !this.graph) {
        console.warn('数据或图形实例未准备好')
        return
      }

      console.log('开始渲染工作流:', this.workflowData)

      // 清空画布
      this.graph.clearCells()

      // 渲染节点
      this.workflowData.nodes.forEach(node => {
        const isEvent = node.label === '开始' || node.label === '结束'
        const nodeConfig = {
          id: node.id,
          shape: isEvent ? 'event' : 'activity',
          x: node.x,
          y: node.y,
          width: node.width,
          height: node.height,
          data: node.data,
          attrs: {
            body: {
              fill: node.label === '开始' || node.label === '结束' ? '#fff' : '#EFF4FF',
              stroke: node.label === '结束' ? '#FF5F5F' : '#5F95FF',
              strokeWidth: 2,
              rx: isEvent ? undefined : 6,
              ry: isEvent ? undefined : 6
            },
            label: {
              text: node.data?.name || node.label,
              fill: '#333',
              fontSize: 12,
              textAnchor: 'middle',
              textVerticalAnchor: 'middle'
            }
          }
        }

        console.log('添加节点:', nodeConfig)
        this.graph.addNode(nodeConfig)
      })

      // 渲染连线
      this.workflowData.edges.forEach(edge => {
        const edgeConfig = {
          source: edge.source,
          target: edge.target,
          vertices: edge.vertices,
          data: edge.data,
          router: edge.router || {
            name: 'normal'
          },
          connector: edge.connector || {
            name: 'normal'
          },
          attrs: {
            line: {
              stroke: '#3c4260',
              strokeWidth: 2,
              targetMarker: 'classic',
            }
          },
          labels: edge.data?.name ? [{
            attrs: {
              text: {
                text: edge.data.name,
                fill: '#333',
                fontSize: 12,
                textAnchor: 'middle',
                textVerticalAnchor: 'middle'
              },
              rect: {
                fill: '#fff',
                stroke: '#dcdfe6',
                strokeWidth: 1,
                rx: 3,
                ry: 3,
                refWidth: '100%',
                refHeight: '100%',
                refX: 0,
                refY: 0
              }
            },
            position: {
              distance: 0.5
            }
          }] : []
        }

        console.log('添加连线:', edgeConfig)
        this.graph.addEdge(edgeConfig)
      })
    },
    showProperties(type, data) {
      this.selectedType = type
      this.selectedData = data
      this.drawerTitle = type === 'activity' ? '活动属性' : '变迁属性'
      this.drawerVisible = true
    },
    getUserName(userId) {
      const user = this.userOptions.find(u => u.value === userId)
      return user ? user.label : userId
    },
    getDeptName(deptId) {
      const dept = this.departmentOptions.find(d => d.value === deptId)
      return dept ? dept.label : deptId
    },
    getOperationName(op) {
      const opMap = {
        'approve': '通过',
        'returnToApplicant': '退回申请人',
        'returnToPrevious': '退回上一步',
        'reject': '不通过'
      }
      return opMap[op] || op
    }
  },
  beforeUnmount() {
    window.removeEventListener('resize', this.updateGraphSize)
  }
}
</script>

<style scoped>
.workflow-viewer {
  padding: 20px;
  height: calc(100vh - 40px);
  display: flex;
  flex-direction: column;
}

.header {
  margin-bottom: 20px;
}

#container {
  flex: 1;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  background: #fafafa;
  min-height: 500px;
}

.drawer-container {
  padding: 20px;
}

.property-item {
  margin-bottom: 16px;
}

.property-item label {
  display: block;
  color: #606266;
  margin-bottom: 8px;
}

.value-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

:deep(.el-tag) {
  margin-right: 8px;
  margin-bottom: 8px;
}
</style> 