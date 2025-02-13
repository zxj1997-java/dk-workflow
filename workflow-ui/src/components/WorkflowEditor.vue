<template>
  <div class="workflow-editor">
    <!-- 左侧工具栏 -->
    <div class="tools-panel">
      <div class="panel-header">
        <h2 class="title">{{ id ? '编辑工作流' : '新建工作流' }}</h2>
      </div>
      <div class="event-circle" draggable="true" @dragstart="onDragStart($event, 'start')">
        <span>开始</span>
      </div>
      <div class="activity-rect" draggable="true" @dragstart="onDragStart($event, 'activity')">
        <span>活动</span>
      </div>
      <div class="event-circle end" draggable="true" @dragstart="onDragStart($event, 'end')">
        <span>结束</span>
      </div>
    </div>

    <!-- 右侧画布 -->
    <div class="canvas-panel">
      <div class="toolbar">
        <button class="save-btn" @click="saveWorkflow">保存</button>
      </div>
      <div id="container" ref="container"></div>
    </div>

    <!-- 添加名称输入对话框 -->
    <el-dialog
      v-model="dialogVisible"
      title="保存工作流"
      width="30%"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      :show-close="false"
    >
      <el-form :model="form" label-width="80px">
        <el-form-item label="流程名称" required>
          <el-input v-model="form.name" placeholder="请输入流程名称"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="cancelSave">取消</el-button>
          <el-button type="primary" @click="confirmSave">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 替换原来的右键菜单 -->
    <node-context-menu
      :visible="contextMenuVisible"
      :x="Number(contextMenuStyle.left.replace('px', ''))"
      :y="Number(contextMenuStyle.top.replace('px', ''))"
      @configure="openActivityDrawer"
      @delete="deleteNode"
    />

    <!-- 添加右侧抽屉 -->
    <activity-drawer
      v-model="drawerVisible"
      :activity-data="currentNode?.data"
      :user-options="userOptions"
      :department-options="departmentOptions"
      @save="handleActivitySave"
    />

    <!-- 添加边的右键菜单 -->
    <edge-context-menu
      :visible="edgeMenuVisible"
      :x="Number(contextMenuStyle.left.replace('px', ''))"
      :y="Number(contextMenuStyle.top.replace('px', ''))"
      @configure="openTransitionDrawer"
      @delete="deleteEdge"
    />

    <!-- 添加变迁配置抽屉 -->
    <transition-drawer
      v-model="transitionDrawerVisible"
      :transition-data="currentEdge?.data"
      @save="saveTransitionConfig"
    />
  </div>
</template>

<script>
import { Graph } from '@antv/x6'
// 修改导入方式，分别导入需要的 API
import {getWorkflowById,saveWorkflow} from '@/api/workflow/workflow'
import { getUsers,getDepartments } from '@/api/workflow/system'
import { ElMessage, ElMessageBox } from 'element-plus'
import EdgeContextMenu from './menu/EdgeContextMenu.vue'
import NodeContextMenu from './menu/NodeContextMenu.vue'
import TransitionDrawer from './drawer/TransitionDrawer.vue'
import ActivityDrawer from './drawer/ActivityDrawer.vue'

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
  name: 'WorkflowEditor',
  components: {
    EdgeContextMenu,
    NodeContextMenu,
    TransitionDrawer,
    ActivityDrawer
  },
  props: {
    id: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      graph: null,
      containerWidth: 0,
      containerHeight: 0,
      selectedCell: null,
      hasStartNode: false,
      hasEndNode: false,
      workflowData: null,
      dialogVisible: false,
      form: {
        name: ''
      },
      flowData: null,
      contextMenuVisible: false,
      contextMenuStyle: {
        left: '0px',
        top: '0px'
      },
      drawerVisible: false,
      currentNode: null,
      activityForm: {
        name: '',
        code: '',
        approvers: [],
        departments: [],
        operations: ['approve', 'returnToApplicant', 'returnToPrevious', 'reject']
      },
      userOptions: [],
      departmentOptions: [],
      edgeMenuVisible: false,
      transitionDrawerVisible: false,
      currentEdge: null,
    }
  },
  async created() {
    // 加载用户和部门数据
    await this.loadOptions()
    if (this.id) {
      await this.loadWorkflowData()
    }
  },
  mounted() {
    this.initGraph()
    if (this.workflowData) {
      this.renderWorkflow()
    }
    // 添加窗口大小变化监听
    window.addEventListener('resize', this.updateGraphSize)
    // 添加页面可见性变化监听
    document.addEventListener('visibilitychange', this.handleVisibilityChange)
  },
  beforeUnmount() {
    // 移除所有事件监听
    window.removeEventListener('resize', this.updateGraphSize)
    document.removeEventListener('visibilitychange', this.handleVisibilityChange)
  },
  methods: {
    updateGraphSize() {
      const container = this.$refs.container
      if (container && this.graph) {
        // 获取容器的新尺寸
        const { width, height } = container.getBoundingClientRect()
        this.containerWidth = width
        this.containerHeight = height
        // 更新画布大小
        this.graph.resize(width, height)
        // 更新节点拖拽限制范围
        this.graph.options.translating.restriction = {
          x: 0,
          y: 0,
          width,
          height
        }
      }
    },
    handleVisibilityChange() {
      if (document.visibilityState === 'visible') {
        // 当页面变为可见时，延迟一下再更新大小，确保 DOM 已经完全恢复
        setTimeout(() => {
          this.updateGraphSize()
        }, 100)
      }
    },
    initGraph() {
      const container = this.$refs.container
      const { width, height } = container.getBoundingClientRect()
      this.containerWidth = width
      this.containerHeight = height

      this.graph = new Graph({
        container: container,
        width: this.containerWidth,
        height: this.containerHeight,
        grid: {
          size: 10,
          visible: true,
          type: 'dot',
          args: {
            color: '#cccccc',
            thickness: 1,
          },
        },
        connecting: {
          router: {
            name: 'normal'
          },
          connector: {
            name: 'normal'
          },
          connectionPoint: {
            name: 'boundary',
            args: {
              sticky: true
            }
          },
          anchor: 'center',
          allowBlank: false,
          snap: {
            radius: 20
          },
          validateConnection: ({ sourceView, targetView, sourceCell }) => {
            if (!sourceView || !targetView) {
              return false
            }
            if (sourceView === targetView) {
              return false
            }

            const sourceType = sourceCell.attrs.label.text
            const targetType = targetView.cell.attrs.label.text

            // 连线规则：
            // 1. 开始节点只能连接到活动
            // 2. 活动可以连接到活动或结束节点
            // 3. 结束节点不能作为源节点
            // 4. 不能形成循环连接

            if (sourceType === '开始' && targetType !== '活动') {
              ElMessage.warning('开始节点只能连接到活动')
              return false
            }

            if (sourceType === '结束') {
              ElMessage.warning('结束节点不能作为起点')
              return false
            }

            if (sourceType === '活动') {
              if (targetType === '开始') {
                ElMessage.warning('不能连接到开始节点')
                return false
              }
            }

            return true
          },
          createEdge() {
            return this.createEdge({
              shape: 'edge',
              attrs: {
                line: {
                  stroke: '#3c4260',
                  strokeWidth: 2,
                  targetMarker: 'classic',
                }
              },
              zIndex: -1
            })
          }
        },
        // 限制节点拖拽范围
        translating: {
          restrict: true,
          restriction: {
            x: 0,
            y: 0,
            width: this.containerWidth,
            height: this.containerHeight
          }
        }
      })

      // 添加连接完成的事件处理
      this.graph.on('edge:connected', ({ edge, isNew }) => {
        if (isNew) {
          const sourceCell = edge.getSourceCell()

          // 只检查开始节点的连接数量限制
          if (sourceCell.attrs.label.text === '开始') {
            const edges = this.graph.getConnectedEdges(sourceCell, { outgoing: true })
            if (edges.length > 1) {
              alert('开始节点只能连接一个活动')
              this.graph.removeCell(edge)
              return
            }
          }
        }
      })

      // 添加连接点
      this.graph.on('node:added', ({ node }) => {
        if (node.shape === 'circle') {
          // 开始节点只有输出点，结束节点只有输入点
          const ports = node.attrs.label.text === '开始' ? 
            [{ group: 'out', position: { name: 'right' } }] :
            [{ group: 'in', position: { name: 'left' } }]
          
          node.addPorts(ports)
        } else if (node.shape === 'rect') {
          // 活动节点四边各添加一个中间连接桩
          const ports = [
            // 左边连接桩
            { group: 'in', args: { x: 0, y: '50%' } },
            // 右边连接桩
            { group: 'in', args: { x: '100%', y: '50%' } },
            // 上边连接桩
            { group: 'in', args: { x: '50%', y: 0 } },
            // 下边连接桩
            { group: 'in', args: { x: '50%', y: '100%' } }
          ]
          node.addPorts(ports)
        }
      })

      // 修改键盘删除功能
      document.addEventListener('keydown', async (e) => {
        if ((e.key === 'Delete') && this.selectedCell) {
          try {
            await ElMessageBox.confirm('确定要删除该节点吗？', '提示', {
              confirmButtonText: '确定',
              cancelButtonText: '取消',
              type: 'warning'
            })
            this.graph.removeCell(this.selectedCell)
            this.selectedCell = null
          } catch {
            // 用户取消删除
          }
        }
      })

      // 修改节点选择功能
      this.graph.on('node:click', ({ node }) => {
        this.clearSelection()
        // 选择当前点击的节点
        node.addTools({
          name: 'boundary',
          args: {
            padding: 5,
            attrs: {
              fill: '#7c68fc',
              stroke: '#333',
              'stroke-width': 1,
              'fill-opacity': 0.2,
            },
          },
        })
        
        // 根据节点类型设置高亮边框颜色
        const nodeText = node.attrs.label.text
        if (nodeText === '结束') {
          node.attr('body/stroke', '#ff4d4f')
        } else {
        node.attr('body/stroke', '#1890ff')
        }
        
        this.selectedCell = node
      })

      // 修改边选择功能
      this.graph.on('edge:click', ({ edge }) => {
        this.clearSelection()
        // 添加边的工具
        edge.addTools([
          {
            name: 'boundary',
            args: {
              padding: 5,
              attrs: {
                stroke: '#1890ff',
                'stroke-width': 1,
                'stroke-dasharray': '5,5',
              },
            },
          },
          {
            name: 'vertices',
            args: {
              attrs: { 
                fill: '#1890ff',
                stroke: '#fff',
                strokeWidth: 1,
              },
            },
          },
          {
            name: 'segments',
            args: {
              attrs: { 
                fill: '#1890ff',
                stroke: '#fff',
                strokeWidth: 1,
              },
            },
          },
        ])
        edge.attr('line/stroke', '#1890ff')
        this.selectedCell = edge
      })

      // 修改空白处点击取消选择
      this.graph.on('blank:click', () => {
        this.clearSelection()
        this.hideContextMenu()
      })

      // 监听画布拖拽
      this.$refs.container.addEventListener('dragover', this.onDragOver)
      this.$refs.container.addEventListener('drop', this.onDrop)

      // 添加连线顶点变化的事件处理
      this.graph.on('edge:changed', ({ edge }) => {
        // 当连线的路由点发生变化时，可以在这里处理
        console.log('连线更新:', {
          vertices: edge.getVertices(),
          router: edge.router,
          connector: edge.connector
        })
      })

      // 添加节点右键菜单事件
      this.graph.on('node:contextmenu', ({ e, node }) => {
        // 修改判断条件：检查节点类型而不是标签文本
        const nodeType = node.data?.type || 
                        (node.attrs.label.text === '开始' ? 'start' : 
                         node.attrs.label.text === '结束' ? 'end' : 'activity')
        
        if (nodeType === 'activity') {
          e.preventDefault()
          this.currentNode = node
          this.contextMenuStyle.left = `${e.clientX}px`
          this.contextMenuStyle.top = `${e.clientY}px`
          this.contextMenuVisible = true
          this.edgeMenuVisible = false
        }
      })

      // 添加边的右键菜单事件
      this.graph.on('edge:contextmenu', ({ e, edge }) => {
        // 检查是否是活动之间的连线
        const sourceCell = edge.getSourceCell()
        const targetCell = edge.getTargetCell()
        
        // 修改类型判断逻辑
        const sourceType = sourceCell.data?.type || 
                          (sourceCell.attrs.label.text === '开始' ? 'start' : 
                           sourceCell.attrs.label.text === '结束' ? 'end' : 'activity')
        const targetType = targetCell.data?.type || 
                          (targetCell.attrs.label.text === '开始' ? 'start' : 
                           targetCell.attrs.label.text === '结束' ? 'end' : 'activity')

        if (sourceType === 'activity' && targetType === 'activity') {
          e.preventDefault()
          this.currentEdge = edge
          this.contextMenuStyle.left = `${e.clientX}px`
          this.contextMenuStyle.top = `${e.clientY}px`
          this.edgeMenuVisible = true
          this.contextMenuVisible = false
        }
      })

      // 修改空白处点击事件
      this.graph.on('blank:click', () => {
        this.clearSelection()
        this.hideContextMenu()
      })

      // 添加节点点击事件处理
      this.graph.on('node:click', () => {
        this.hideContextMenu()
      })

      // 添加边点击事件处理
      this.graph.on('edge:click', () => {
        this.hideContextMenu()
      })
    },
    onDragStart(event, type) {
      if (type === 'start' && this.hasStartNode) {
        ElMessage.warning('只能添加一个开始节点')
        event.preventDefault()
        return
      }
      if (type === 'end' && this.hasEndNode) {
        ElMessage.warning('只能添加一个结束节点')
        event.preventDefault()
        return
      }
      event.dataTransfer.setData('type', type)
    },
    onDragOver(event) {
      event.preventDefault()
    },
    onDrop(event) {
      const type = event.dataTransfer.getData('type')
      const container = this.$refs.container
      const rect = container.getBoundingClientRect()
      
      // 计算相对于容器的坐标
      const x = event.clientX - rect.left
      const y = event.clientY - rect.top
      
      // 检查是否在容器范围内
      if (x < 0 || x > this.containerWidth || y < 0 || y > this.containerHeight) {
        return
      }

      // 检查开始和结束节点的数量限制
      if (type === 'start' && this.checkNodeExists('start')) {
        alert('只能添加一个开始节点')
        return
      }
      if (type === 'end' && this.checkNodeExists('end')) {
        alert('只能添加一个结束节点')
        return
      }

      switch(type) {
        case 'start':
          this.graph.addNode({
            shape: 'circle',
            x: x,
            y: y,
            width: 40,
            height: 40,
            ports: {
              groups: {
                out: {
                  position: 'right',
                  attrs: {
                    circle: {
                      r: 4,
                      magnet: true,
                      stroke: '#5F95FF',
                      strokeWidth: 1,
                      fill: '#fff',
                    }
                  }
                }
              }
            },
            attrs: {
              body: {
                fill: '#fff',
                stroke: '#5F95FF',
                strokeWidth: 2
              },
              label: {
                text: '开始',
                fill: '#333',
                fontSize: 12,
                textAnchor: 'middle',
                textVerticalAnchor: 'middle'
              }
            }
          })
          break
        case 'activity':
          this.graph.addNode({
            shape: 'rect',
            x: x,
            y: y,
            width: 80,
            height: 40,
            ports: {
              groups: {
                in: {
                  position: 'absolute',
                  attrs: {
                    circle: {
                      r: 4,
                      magnet: true,
                      stroke: '#5F95FF',
                      strokeWidth: 1,
                      fill: '#fff',
                    }
                  }
                }
              },
              items: [
                // 左边连接桩
                { group: 'in', args: { x: 0, y: '50%' } },
                // 右边连接桩
                { group: 'in', args: { x: '100%', y: '50%' } },
                // 上边连接桩
                { group: 'in', args: { x: '50%', y: 0 } },
                // 下边连接桩
                { group: 'in', args: { x: '50%', y: '100%' } }
              ]
            },
            attrs: {
              body: {
                fill: '#EFF4FF',
                stroke: '#5F95FF',
                strokeWidth: 2,
                rx: 6,
                ry: 6
              },
              label: {
                text: '活动',
                fill: '#262626',
                fontSize: 12,
                textAnchor: 'middle',
                textVerticalAnchor: 'middle'
              }
            }
          })
          break
        case 'end':
          this.graph.addNode({
            shape: 'circle',
            x: x,
            y: y,
            width: 40,
            height: 40,
            ports: {
              groups: {
                in: {
                  position: 'left',
                  attrs: {
                    circle: {
                      r: 4,
                      magnet: true,
                      stroke: '#5F95FF',
                      strokeWidth: 1,
                      fill: '#fff',
                    }
                  }
                }
              }
            },
            attrs: {
              body: {
                fill: '#fff',
                stroke: '#FF5F5F',
                strokeWidth: 2
              },
              label: {
                text: '结束',
                fill: '#333',
                fontSize: 12,
                textAnchor: 'middle',
                textVerticalAnchor: 'middle'
              }
            }
          })
          break
      }
    },
    checkNodeExists(type) {
      const nodes = this.graph.getNodes()
      if (type === 'start') {
        return nodes.some(node => node.attrs.label.text === '开始')
      } else if (type === 'end') {
        return nodes.some(node => node.attrs.label.text === '结束')
      }
      return false
    },
    clearSelection() {
      if (this.selectedCell) {
        // 移除工具
        this.selectedCell.removeTools()
        
        if (this.selectedCell.isNode()) {
          // 恢复节点的默认样式
          const nodeText = this.selectedCell.attrs.label.text
          if (nodeText === '结束') {
            this.selectedCell.attr('body/stroke', '#FF5F5F')
          } else {
          this.selectedCell.attr('body/stroke', '#5F95FF')
          }
        } else if (this.selectedCell.isEdge()) {
          // 恢复边的默认样式
          this.selectedCell.attr('line/stroke', '#3c4260')
        }
      }
      this.selectedCell = null
    },
    async saveWorkflow() {
      try {
        const nodes = this.graph.getNodes()
        const edges = this.graph.getEdges()
        
        const flowData = {
          nodes: nodes.map(node => ({
            id: node.id,
            shape: node.shape,
            x: node.position().x,
            y: node.position().y,
            width: node.size().width,
            height: node.size().height,
            label: node.attrs.label.text,
            data: node.data,
            type: node.data?.type || 
                  (node.attrs.label.text === '开始' ? 'start' : 
                   node.attrs.label.text === '结束' ? 'end' : 'activity')
          })),
          edges: edges.map(edge => ({
            source: edge.getSourceCell().id,
            target: edge.getTargetCell().id,
            vertices: edge.getVertices() || [],
            data: edge.data,
            router: {
              name: edge.router?.name || 'normal'
            },
            connector: {
              name: edge.connector?.name || 'normal'
            }
          }))
        }

        // 获取当前工作流的名称
        let workflowName = this.workflowData?.name || '新建工作流'
        
        await saveWorkflow({
          id: this.id,
          name: workflowName,
          flowData: JSON.stringify(flowData)
        })

        ElMessage.success('保存成功')
      } catch (error) {
        console.error('保存失败:', error)
        ElMessage.error('保存失败')
      }
    },
    async loadWorkflowData() {
      try {
        const data = await getWorkflowById(this.id)
        console.log("工作流数据为空",data)
        if (!data.flowData) {
          throw new Error('工作流数据为空')
        }
        this.workflowData = JSON.parse(data.flowData)
        // 确保在数据加载后立即渲染
        this.$nextTick(() => {
          this.renderWorkflow()
        })
      } catch (error) {
        console.error('加载工作流数据失败:', error)
      }
    },
    renderWorkflow() {
      if (!this.workflowData || !this.graph) return

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
          // 恢复活动配置数据
          data: node.data,  // 添加这行，恢复节点配置数据
          ports: {
            groups: {
              in: {
                position: 'absolute',
                attrs: {
                  circle: {
                    r: 4,
                    magnet: true,
                    stroke: '#5F95FF',
                    strokeWidth: 1,
                    fill: '#fff',
                  }
                }
              },
              out: {
                position: 'absolute',
                attrs: {
                  circle: {
                    r: 4,
                    magnet: true,
                    stroke: '#5F95FF',
                    strokeWidth: 1,
                    fill: '#fff',
                  }
                }
              }
            },
            items: node.label === '开始' ? [
              { group: 'out', args: { x: '100%', y: '50%' } }
            ] : node.label === '结束' ? [
              { group: 'in', args: { x: 0, y: '50%' } }
            ] : [
              { group: 'in', args: { x: 0, y: '50%' } },
              { group: 'in', args: { x: '100%', y: '50%' } },
              { group: 'in', args: { x: '50%', y: 0 } },
              { group: 'in', args: { x: '50%', y: '100%' } }
            ]
          },
          attrs: {
            body: {
              fill: node.label === '开始' || node.label === '结束' ? '#fff' : '#EFF4FF',
              stroke: node.label === '结束' ? '#FF5F5F' : '#5F95FF',
              strokeWidth: 2,
              rx: isEvent ? undefined : 6,
              ry: isEvent ? undefined : 6
            },
            label: {
              text: node.data?.name || node.label,  // 使用配置的名称或默认标签
              fill: '#333',
              fontSize: 12,
              textAnchor: 'middle',
              textVerticalAnchor: 'middle'
            }
          }
        }

        console.log('添加节点:', nodeConfig)
        this.graph.addNode(nodeConfig)

        // 更新节点状态
        if (node.label === '开始') {
          this.hasStartNode = true
        } else if (node.label === '结束') {
          this.hasEndNode = true
        }
      })

      // 渲染连线
      this.workflowData.edges.forEach(edge => {
        console.log('添加连线:', edge)
        this.graph.addEdge({
          source: edge.source,
          target: edge.target,
          vertices: edge.vertices,
          data: edge.data, // 恢复边的数据
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
          // 添加标签显示
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
              distance: 0.5 // 标签位于连线中间
            }
          }] : []
        })
      })
    },
    showContextMenu(x, y) {
      this.contextMenuStyle.left = `${x}px`
      this.contextMenuStyle.top = `${y}px`
    },
    hideContextMenu() {
      this.contextMenuVisible = false
      this.edgeMenuVisible = false
    },
    openActivityDrawer() {
      this.hideContextMenu()
      this.drawerVisible = true
    },
    async handleActivitySave(formData) {
      try {
        if (this.currentNode) {
          // 更新节点数据
          this.currentNode.setData(formData)
          // 更新节点显示的名称
          this.currentNode.attr('label/text', formData.name)
          // 关闭抽屉
          this.drawerVisible = false
          
          // 保存整个工作流
          await this.saveWorkflow()
          
          ElMessage.success('活动配置保存成功')
        }
      } catch (error) {
        console.error('保存活动失败:', error)
        ElMessage.error('保存失败')
      }
    },
    async deleteNode() {
      try {
        await ElMessageBox.confirm('确定要删除该节点吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        this.graph.removeCell(this.currentNode)
        this.hideContextMenu()
      } catch {
        // 用户取消删除
      }
    },
    async loadOptions() {
      try {
        // 修改 API 调用方式
        const [users, departments] = await Promise.all([
          getUsers(),
          getDepartments()
        ])
        this.userOptions = users
        this.departmentOptions = departments
      } catch (error) {
        console.error('加载选项数据失败:', error)
        ElMessage.error('加载人员和部门数据失败')
      }
    },
    openTransitionDrawer() {
      this.hideContextMenu()
      this.transitionDrawerVisible = true
    },
    async deleteEdge() {
      try {
        await ElMessageBox.confirm('确定要删除该连线吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        this.graph.removeCell(this.currentEdge)
        this.hideContextMenu()
      } catch {
        // 用户取消删除
      }
    },
    async saveTransitionConfig(config) {
      try {
        if (this.currentEdge) {
          // 更新边的数据
          this.currentEdge.setData(config)
          // 更新连线标签
          if (config.name) {
            this.currentEdge.setLabels([{
              attrs: {
                text: {
                  text: config.name,
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
            }])
          }
          
          // 保存整个工作流
          await this.saveWorkflow()
          
          this.transitionDrawerVisible = false
          ElMessage.success('变迁配置保存成功')
        }
      } catch (error) {
        console.error('保存变迁失败:', error)
        ElMessage.error('保存失败')
      }
    },
  }
}
</script>

<style scoped>
.workflow-editor {
  display: flex;
  width: 100%;
  height: calc(100vh - 10px);
  border: 1px solid #dcdfe6;
  margin: 0 20px 20px 20px;
  overflow: hidden;
}

.tools-panel {
  width: 130px;
  background: #f5f7fa;
  border-right: 1px solid #dcdfe6;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.panel-header {
  padding: 16px;
  border-bottom: 1px solid #dcdfe6;
}

.title {
  margin: 0;
  font-size: 16px;
  color: #333;
  font-weight: normal;
}

.tools-panel > div:not(.panel-header) {
  margin: 20px;
}

.toolbar {
  position: absolute;
  top: 20px;
  right: 20px;
  z-index: 100;
}

.save-btn {
  padding: 8px 16px;
  background-color: #1890ff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  margin-right: 20px;
}

.save-btn:hover {
  background-color: #40a9ff;
}

.save-btn:active {
  background-color: #096dd9;
}

.canvas-panel {
  flex: 1;
  background: #fff;
  position: relative;
  min-width: 600px;
  overflow: hidden;
}

#container {
  width: 100%;
  height: 100%;
  position: absolute;
  top: 0;
  left: 0;
  background: #fafafa;
}

.event-circle {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  border: 2px solid #5F95FF;
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: move;
}

.event-circle.end {
  border-color: #FF5F5F;
}

.event-circle span {
  font-size: 12px;
  color: #333;
  user-select: none;
}

.activity-rect {
  width: 80px;
  height: 40px;
  border: 2px solid #5F95FF;
  border-radius: 6px;
  background: #EFF4FF;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: move;
}

.activity-rect span {
  font-size: 12px;
  color: #262626;
  user-select: none;
}

.event-circle:hover {
  border-color: #1890ff;
}

.event-circle.end:hover {
  border-color: #ff4d4f;
}

.activity-rect:hover {
  border-color: #1890ff;
  background: #e6f7ff;
}

/* 美化滚动条样式 */
.tools-panel::-webkit-scrollbar {
  width: 6px;
}

.tools-panel::-webkit-scrollbar-thumb {
  background: #dcdfe6;
  border-radius: 3px;
}

.tools-panel::-webkit-scrollbar-track {
  background: #f5f7fa;
}

/* 添加选中节点的样式 */
:deep(.x6-node-selected) {
  box-shadow: 0 0 0 2px #1890ff;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.activity-drawer-modal {
  animation: none !important;
}
</style> 