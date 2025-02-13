<template>
  <div class="instance-viewer">
    <div class="header">
      <h2>{{ workflow?.name }} - 流程实例</h2>
      <div class="status-tag">
        <el-tag :type="getStatusType(approvalRecords[0]?.status)">
          {{ getStatusText(approvalRecords[0]?.status) }}
        </el-tag>
      </div>
    </div>

    <div class="content">
      <div class="main-content">
        <div class="graph-container">
          <div class="graph-wrapper">
            <div id="container" ref="container"></div>
          </div>
        </div>

        <div class="history-list">
          <div class="list-header">
            <h3>审批历史</h3>
          </div>
          <el-table :data="approvalRecords" stripe style="width: 100%" @row-click="handleRowClick">
            <el-table-column prop="activity.name" label="节点" width="150" />
            <el-table-column prop="approver" label="审批人" width="120" />
            <el-table-column prop="status" label="操作" width="120">
              <template #default="{ row }">
                <el-tag :type="getTimelineItemType(row.status)">
                  {{ getActionText(row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="操作时间" width="180">
              <template #default="{ row }">
                {{ formatDate(row.createTime) }}
              </template>
            </el-table-column>
            <el-table-column prop="comment" label="审批意见" min-width="200" show-overflow-tooltip />
          </el-table>
        </div>
      </div>

      <div class="approval-panel" v-if="selectedNode">
        <div class="panel-header">
          <h3>{{ selectedNode.data.name }} - 审批记录</h3>
          <el-button 
            type="text" 
            class="close-btn"
            @click="selectedNode = null"
          >
            <el-icon><Close /></el-icon>
          </el-button>
        </div>
        <div class="panel-content">
          <el-timeline>
            <el-timeline-item
              v-for="record in nodeApprovalRecords"
              :key="record.id"
              :timestamp="formatDate(record.createTime)"
              :type="getTimelineItemType(record.status)"
            >
              <div class="timeline-content">
                <div class="approver">审批人：{{ record.approver }}</div>
                <div class="action">操作：{{ getActionText(record.status) }}</div>
                <div class="comment">意见：{{ record.comment }}</div>
              </div>
            </el-timeline-item>
            <el-empty v-if="nodeApprovalRecords.length === 0" description="暂无审批记录" />
          </el-timeline>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
// eslint-disable-next-line no-unused-vars
import { Graph } from '@antv/x6'
import { workflowApi } from '@/api/workflow'
import { ElMessage } from 'element-plus'
import { Close } from '@element-plus/icons-vue'

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
        r: 20  // 添加圆形半径
      },
    },
  },
  true,
)

Graph.registerNode(
  'activity',
  {
    inherit: 'rect',
    width: 120,  // 添加默认宽度
    height: 60,  // 添加默认高度
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
        textWrap: {
          width: -10,  // 文本宽度留边距
          height: -10, // 文本高度留边距
          ellipsis: true // 文本超出显示省略号
        }
      },
    },
  },
  true,
)

export default {
  name: 'InstanceViewer',
  components: {
    Close
  },
  props: {
    instanceId: {
      type: String,
      required: true
    }
  },
  data() {
    return {
      graph: null,
      workflow: null,
      workflowData: null,
      approvalRecords: [],
      selectedNode: null
    }
  },
  computed: {
    nodeApprovalRecords() {
      if (!this.selectedNode || !this.approvalRecords) return [];
      return this.approvalRecords.filter(record => {
        // 尝试获取审批记录中关联的活动ID，优先使用 nodeId 字段
        let actId = null;
        if (record.activity && record.activity.nodeId) {
          actId = record.activity.nodeId;
        } else if (record.activity && record.activity.id) {
          actId = record.activity.id;
        } else if (record.activityId) {
          actId = record.activityId;
        }
        return actId === this.selectedNode.id;
      });
    }
  },
  async created() {
    await this.loadInstanceData()
  },
  mounted() {
    this.$nextTick(() => {
      this.initGraph()
      if (this.workflowData) {
        this.renderWorkflow()
      }
    })
  },
  methods: {
    async loadInstanceData() {
      try {
        console.log('加载运行时任务，业务ID:', this.instanceId)
        // 查询运行时任务（即审批记录），这里 businessId 为离职申请的记录ID
        const res = await workflowApi.getRuntimeTasks(this.instanceId)
        console.log('运行时任务数据:', res)
        this.approvalRecords = res.data || []
        // 获取流程定义（假设通过流程编码获取，此处为 leave 工作流）
        const wfRes = await workflowApi.getWorkflowByCode('leave')
        this.workflow = wfRes.data
        console.log('获取流程定义:', this.workflow)
        if (this.workflow.flowData) {
          this.workflowData = JSON.parse(this.workflow.flowData)
          if (this.graph) {
            this.renderWorkflow()
          }
        }
      } catch (error) {
        console.error('加载数据失败:', error)
        ElMessage.error('加载数据失败')
      }
    },
    
    initGraph() {
      const container = this.$refs.container
      if (!container) return

      const { width, height } = container.getBoundingClientRect()
      
      this.graph = new Graph({
        container: container,
        width: width || 800,
        height: height || 400,  // 减小默认高度
        grid: {
          size: 10,
          visible: true,
          type: 'dot',
          args: {
            color: '#cccccc',
            thickness: 1,
          },
        },
        interacting: {
          nodeMovable: false,
          edgeLabelMovable: false,
          magnetConnectable: false,
          toolsAddable: false,
          vertexAddable: false,
          vertexMovable: false,
          vertexDeletable: false,
          edgeMovable: false,
          edgeDeletable: false
        },
        connecting: {
          router: 'manhattan',
          connector: {
            name: 'rounded',
            args: {
              radius: 8
            }
          }
        },
        background: {
          color: '#fafafa'
        },
        scaling: {
          min: 0.2,
          max: 2
        },
        autoResize: true,
        panning: true,
        mousewheel: {
          enabled: true,
          modifiers: ['ctrl', 'meta']
        }
      })

      console.log('Graph initialized:', this.graph)

      // 添加节点点击事件，设置高亮效果并更新选中节点数据
      this.graph.on('node:click', ({ node }) => {
        const nodeData = node.getData()
        console.log('Node clicked:', nodeData)
        if (nodeData && nodeData.type !== 'event') {  // 排除开始和结束节点
          // 清除所有节点的高亮效果（恢复 strokeWidth 为 2）
          this.graph.getNodes().forEach(n => {
            n.setAttrs({ body: { strokeWidth: 2 } })
          })
          // 高亮当前点击的节点
          node.setAttrs({ body: { strokeWidth: 4 } })
          // 更新选中节点数据
          this.selectedNode = {
            id: node.id,
            data: nodeData
          }
        }
      })

      // 添加窗口大小变化监听
      window.addEventListener('resize', this.updateGraphSize)
    },

    updateGraphSize() {
      if (!this.graph || !this.$refs.container) return
      const { width, height } = this.$refs.container.getBoundingClientRect()
      this.graph.resize(width, height)
    },
    
    renderWorkflow() {
      if (!this.workflowData || !this.graph) {
        console.warn('无法渲染流程：缺少数据或 graph 为空')
        return
      }
      
      console.log('开始渲染流程')
      this.graph.clearCells()

      // 计算每个节点的状态：completed, pending, notActive
      const nodeStates = {}
      this.workflowData.nodes.forEach(node => {
        let state = 'notActive'
        // 开始节点直接设为 completed
        if (node.label === '开始') {
          state = 'completed'
        } else {
          const record = this.approvalRecords.find(r => {
            // 尝试获取审批记录中关联的活动ID，优先使用 nodeId
            let actId = null
            if (r.activity && r.activity.nodeId) {
              actId = r.activity.nodeId
            } else if (r.activity && r.activity.id) {
              actId = r.activity.id
            } else if (r.activityId) {
              actId = r.activityId
            }
            return actId === node.id
          })
          if (record) {
            if (record.status === 'COMPLETED') {
              state = 'completed'
            } else if (record.status === 'PENDING') {
              state = 'pending'
            }
          }
        }
        nodeStates[node.id] = state
      })

      // 渲染节点（使用与 WorkflowViewer.vue 一致的样式，并根据状态设置颜色）
      this.workflowData.nodes.forEach(node => {
        const isEvent = node.label === '开始' || node.label === '结束'
        // 根据节点状态设置颜色
        let fillColor, strokeColor
        const state = nodeStates[node.id] || 'notActive'
        if (state === 'completed') {
          fillColor = "#5F95FF"  // 蓝色
          strokeColor = "#5F95FF"
        } else if (state === 'pending') {
          fillColor = "#ffeb99"  // 黄色
          strokeColor = "#ffcc00"
        } else {
          fillColor = "#d3d3d3"  // 灰色
          strokeColor = "#d3d3d3"
        }
        const nodeConfig = {
          id: node.id,
          shape: isEvent ? 'event' : 'activity',
          x: node.x,
          y: node.y,
          width: node.width,
          height: node.height,
          data: { ...node.data, type: isEvent ? 'event' : 'activity' },
          attrs: {
            body: {
              // 对于开始和结束节点保持特殊处理
              fill: isEvent ? (node.label === '开始' ? "#5F95FF" : "#fff") : fillColor,
              stroke: isEvent ? (node.label === '结束' ? "#FF5F5F" : "#5F95FF") : strokeColor,
              strokeWidth: 2,
              rx: isEvent ? undefined : 6,
              ry: isEvent ? undefined : 6
            },
            label: {
              text: node.data && node.data.name ? node.data.name : node.label,
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

      // 渲染连线（根据两端节点状态设置颜色）
      this.workflowData.edges.forEach(edge => {
        const sourceState = nodeStates[edge.source] || 'notActive'
        const targetState = nodeStates[edge.target] || 'notActive'
        
        // 查找源节点和目标节点数据，用于检测是否为 '结束' 节点
        const sourceNode = this.workflowData.nodes.find(n => n.id === edge.source)
        const targetNode = this.workflowData.nodes.find(n => n.id === edge.target)
  
        let edgeColor = "#3c4260"  // 默认颜色
        // 如果连线两端均已激活（即不为未激活状态），或者已完成节点与"结束"节点连接，则设为蓝色
        if (
          (sourceState !== 'notActive' && targetState !== 'notActive') ||
          (sourceState === 'completed' && targetNode && targetNode.label === '结束') ||
          (targetState === 'completed' && sourceNode && sourceNode.label === '结束')
        ) {
          edgeColor = "#5F95FF"
        }
        const edgeConfig = {
          source: edge.source,
          target: edge.target,
          vertices: edge.vertices || [],
          router: edge.router || { name: 'manhattan' },
          connector: edge.connector || { name: 'rounded', args: { radius: 8 } },
          attrs: {
            line: {
              stroke: edgeColor,
              strokeWidth: 2,
              targetMarker: { name: 'classic', size: 8 }
            }
          },
          labels: edge.data && edge.data.name ? [{
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
            position: { distance: 0.5 }
          }] : []
        }
        console.log('创建连线:', edgeConfig)
        this.graph.addEdge(edgeConfig)
      })

      // 自动布局
      this.graph.zoomToFit({ padding: 20 })
      this.graph.centerContent()
    },
    
    getStatusType(status) {
      const typeMap = { 'PENDING': 'warning', 'COMPLETED': 'success', 'TERMINATED': 'danger' }
      return typeMap[status] || 'info'
    },
    
    getStatusText(status) {
      const textMap = { 'PENDING': '待处理', 'COMPLETED': '已完成', 'TERMINATED': '已终止' }
      return textMap[status] || status
    },
    
    getTimelineItemType(status) {
      // 使用运行时任务状态决定时间轴项目颜色
      const typeMap = { 'PENDING': 'warning', 'COMPLETED': 'success', 'TERMINATED': 'danger' }
      return typeMap[status] || 'info'
    },
    
    getActionText(status) {
      const textMap = { 'PENDING': '待处理', 'COMPLETED': '通过', 'TERMINATED': '不通过' }
      return textMap[status] || status
    },
    
    formatDate(dateStr) {
      if (!dateStr) return ''
      const date = new Date(dateStr)
      return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
    },

    handleRowClick(row) {
      const node = this.graph.getCellById(row.activity.id)
      if (node) {
        // 清除之前的高亮
        this.graph.getNodes().forEach(n => {
          n.setAttrs({
            body: {
              strokeWidth: 2
            }
          })
        })
        
        // 高亮当前节点
        node.setAttrs({
          body: {
            strokeWidth: 4
          }
        })
        
        // 更新选中节点
        this.selectedNode = {
          id: node.id,
          data: node.getData()
        }
      }
    }
  },
  beforeUnmount() {
    window.removeEventListener('resize', this.updateGraphSize)
    if (this.graph) {
      this.graph.dispose()
    }
  }
}
</script>

<style scoped>
.instance-viewer {
  padding: 20px;
  height: calc(100vh - 84px);  /* 调整高度 */
  min-height: 600px;  /* 添加最小高度 */
  display: flex;
  flex-direction: column;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  flex-shrink: 0;  /* 防止头部压缩 */
}

.content {
  flex: 1;
  display: flex;
  gap: 20px;
  min-height: 0;  /* 重要：允许内容区域收缩 */
  position: relative;  /* 添加相对定位 */
}

.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
  gap: 20px;
}

.graph-container {
  flex: 1;
  min-height: 400px;
  display: flex;
  flex-direction: column;
}

.graph-wrapper {
  flex: 1;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  background: #fafafa;
  overflow: hidden;  /* 防止内容溢出 */
  position: relative;  /* 相对定位 */
}

#container {
  position: absolute;  /* 绝对定位 */
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
}

.history-list {
  height: 300px;
  background: #fff;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  display: flex;
  flex-direction: column;
}

.list-header {
  padding: 16px;
  border-bottom: 1px solid #dcdfe6;
}

.list-header h3 {
  margin: 0;
  font-size: 16px;
  color: #303133;
}

.approval-panel {
  width: 360px;  /* 稍微减小宽度 */
  background: #fff;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  display: flex;
  flex-direction: column;
  position: absolute;  /* 改为绝对定位 */
  right: 0;
  top: 0;
  bottom: 0;
  z-index: 100;
  box-shadow: -2px 0 8px rgba(0, 0, 0, 0.1);  /* 添加阴影 */
}

.panel-header {
  padding: 16px;
  border-bottom: 1px solid #dcdfe6;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.panel-header h3 {
  margin: 0;
  font-size: 16px;
  color: #303133;
  flex: 1;
}

.close-btn {
  padding: 4px;
  color: #909399;
}

.close-btn:hover {
  color: #606266;
}

.panel-content {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
}

.timeline-content {
  padding: 8px;
  background: #fafafa;
  border-radius: 4px;
}

.approver, .action, .comment {
  margin-bottom: 4px;
  color: #606266;
}

.comment {
  white-space: pre-wrap;
}

:deep(.el-timeline-item__node) {
  width: 12px;
  height: 12px;
}

:deep(.el-timeline-item__tail) {
  left: 5px;
}

:deep(.el-timeline-item__content) {
  margin-left: 25px;
}

:deep(.el-table) {
  flex: 1;
}

:deep(.el-table__header) {
  background-color: #f5f7fa;
}

:deep(.el-table__row) {
  cursor: pointer;
}

:deep(.el-table__row:hover) {
  background-color: #f5f7fa;
}

:deep(.el-table__body-wrapper) {
  overflow-y: auto;
}

:deep(.el-table .cell) {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
</style> 