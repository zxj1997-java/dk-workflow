import { createRouter, createWebHistory } from 'vue-router'
import WorkflowList from '@/views/workflow/List.vue'
import WorkflowEditor from '@/components/WorkflowEditor.vue'
import WorkflowViewer from '@/components/WorkflowViewer.vue'
import InstanceViewer from '@/views/workflow/InstanceViewer.vue'
import InstanceList from '@/views/workflow/InstanceList.vue'

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
    path: '/workflow/instance/:instanceId',
    name: 'InstanceViewer',
    component: InstanceViewer,
    props: true
  },
  {
    path: '/workflow/:id/instances',
    name: 'InstanceList',
    component: InstanceList,
    props: true
  }
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

export default router 