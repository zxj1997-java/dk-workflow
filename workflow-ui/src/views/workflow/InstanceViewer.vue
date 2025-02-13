<template>
  <div class="instance-viewer">
    <div class="header">
      <h2>{{ workflow?.name }} - 流程实例</h2>
      <div class="status-tag">
        <el-tag :type="getStatusType(instance?.status)">{{ getStatusText(instance?.status) }}</el-tag>
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
            <el-table-column prop="nodeName" label="节点" width="150" />
            <el-table-column prop="approver" label="审批人" width="120" />
            <el-table-column prop="action" label="操作" width="120">
              <template #default="{ row }">
                <el-tag :type="getTimelineItemType(row.action)">
                  {{ getActionText(row.action) }}
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
              :type="getTimelineItemType(record.action)"
            >
              <div class="timeline-content">
                <div class="approver">审批人：{{ record.approver }}</div>
                <div class="action">操作：{{ getActionText(record.action) }}</div>
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
import { workflowApi,instanceApi } from '@/api/workflow'
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
      instance: null,
      workflowData: null,
      approvalRecords: [],
      selectedNode: null
    }
  },
  computed: {
    nodeApprovalRecords() {
      if (!this.selectedNode || !this.approvalRecords) return []
      return this.approvalRecords.filter(record => record.nodeId === this.selectedNode.id)
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
        console.log('Loading instance data for ID:', this.instanceId)
        // 加载实例数据和审批记录
        const [instance, records] = await Promise.all([
          instanceApi.getWorkflowInstance(this.instanceId),
          instanceApi.getApprovalRecords(this.instanceId)
        ])
        
        console.log('Instance data:', instance)
        console.log('Approval records:', records)
        
        this.instance = instance
        this.approvalRecords = records
        
        // 加载工作流定义
        this.workflow = await workflowApi.getWorkflowById(instance.workflowId)
        console.log('Workflow data:', this.workflow)
        
        // 解析流程数据
        if (instance.flowData) {
          this.workflowData = JSON.parse(instance.flowData)
          console.log('Parsed flow data:', this.workflowData)
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

      // 添加节点点击事件
      this.graph.on('node:click', ({ node }) => {
        const nodeData = node.getData()
        console.log('Node clicked:', nodeData)
        if (nodeData && nodeData.type !== 'event') {  // 排除开始和结束节点
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
        console.warn('Cannot render workflow: missing data or graph')
        return
      }

      console.log('Rendering workflow')
      this.graph.clearCells()

      // 渲染节点
      this.workflowData.nodes.forEach(node => {
        const isEvent = node.label === '开始' || node.label === '结束'
        const hasApprovals = this.approvalRecords.some(record => record.nodeId === node.id)
        const isCurrentNode = this.instance.currentNode === node.id
        
        console.log('Rendering node:', node)
        
        const nodeConfig = {
          id: node.id,
          shape: isEvent ? 'event' : 'activity',
          x: node.x || 100,  // 提供默认值
          y: node.y || 100,  // 提供默认值
          width: node.width || (isEvent ? 40 : 120),  // 提供默认值
          height: node.height || (isEvent ? 40 : 60),  // 提供默认值
          data: {
            ...node.data,
            type: isEvent ? 'event' : 'activity',
            hasApprovals,
            isCurrentNode
          },
          attrs: {
            body: {
              fill: isCurrentNode ? '#E6F7FF' : (hasApprovals ? '#F6FFED' : '#EFF4FF'),
              stroke: isCurrentNode ? '#1890FF' : (hasApprovals ? '#52C41A' : '#5F95FF'),
              strokeWidth: 2,
              cursor: isEvent ? 'default' : 'pointer'
            },
            label: {
              text: node.data?.name || node.label,
              fill: '#333',
              fontSize: 12,
              cursor: isEvent ? 'default' : 'pointer'
            }
          }
        }

        const createdNode = this.graph.addNode(nodeConfig)
        console.log('Node created:', createdNode)
      })

      // 渲染连线
      this.workflowData.edges.forEach(edge => {
        console.log('Rendering edge:', edge)
        const edgeConfig = {
          source: edge.source,
          target: edge.target,
          router: {
            name: 'manhattan'
          },
          connector: {
            name: 'rounded',
            args: {
              radius: 8
            }
          },
          attrs: {
            line: {
              stroke: '#3c4260',
              strokeWidth: 2,
              targetMarker: {
                name: 'classic',
                size: 8
              }
            }
          }
        }
        const createdEdge = this.graph.addEdge(edgeConfig)
        console.log('Edge created:', createdEdge)
      })

      // 自动布局
      this.graph.zoomToFit({ padding: 20 })
      this.graph.centerContent()
    },
    
    getNodeFillColor(nodeId) {
      const record = this.approvalRecords.find(r => r.nodeId === nodeId)
      if (!record) return '#EFF4FF'
      return nodeId === this.instance.currentNode ? '#E6F7FF' : '#F6FFED'
    },
    
    getNodeStrokeColor(nodeId) {
      const record = this.approvalRecords.find(r => r.nodeId === nodeId)
      if (!record) return '#5F95FF'
      return nodeId === this.instance.currentNode ? '#1890FF' : '#52C41A'
    },
    
    getStatusType(status) {
      const typeMap = {
        'RUNNING': 'primary',
        'COMPLETED': 'success',
        'TERMINATED': 'danger'
      }
      return typeMap[status] || 'info'
    },
    
    getStatusText(status) {
      const textMap = {
        'RUNNING': '进行中',
        'COMPLETED': '已完成',
        'TERMINATED': '已终止'
      }
      return textMap[status] || status
    },
    
    getTimelineItemType(action) {
      const typeMap = {
        'approve': 'success',
        'reject': 'danger',
        'returnToApplicant': 'warning',
        'returnToPrevious': 'warning'
      }
      return typeMap[action] || 'info'
    },
    
    getActionText(action) {
      const textMap = {
        'approve': '通过',
        'reject': '不通过',
        'returnToApplicant': '退回申请人',
        'returnToPrevious': '退回上一步'
      }
      return textMap[action] || action
    },
    
    formatDate(dateStr) {
      if (!dateStr) return ''
      const date = new Date(dateStr)
      return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
    },

    handleRowClick(row) {
      const node = this.graph.getCellById(row.nodeId)
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