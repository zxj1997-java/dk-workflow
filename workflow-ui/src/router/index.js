import { createRouter, createWebHistory } from 'vue-router'
import WorkflowList from '@/views/workflow/List.vue'
import WorkflowEditor from '@/components/WorkflowEditor.vue'
import WorkflowViewer from '@/components/WorkflowViewer.vue'
import InstanceList from '@/views/workflow/InstanceList.vue'
import InstanceDetail from '@/views/workflow/InstanceDetail.vue'

const routes = [
  {
    path: '/',
    redirect: '/workflow/list'
  },
  {
    path: '/workflow/list',
    name: 'WorkflowList',
    component: WorkflowList
  },
  {
    path: '/workflow/editor/:id?',
    name: 'WorkflowEditor',
    component: WorkflowEditor,
    props: true
  },
  {
    path: '/workflow/viewer/:id',
    name: 'WorkflowViewer',
    component: WorkflowViewer,
    props: route => ({
      id: route.params.id,
      version: route.query.version
    })
  },
  {
    path: '/workflow/instance/:workflowId',
    name: 'InstanceList',
    component: InstanceList,
    props: true
  },
  {
    path: '/workflow/instance/detail/:instanceId',
    name: 'InstanceDetail',
    component: InstanceDetail,
    props: true
  }
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

export default router 